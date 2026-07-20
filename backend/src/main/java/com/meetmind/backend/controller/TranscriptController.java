package com.meetmind.backend.controller;

import com.meetmind.backend.dto.SummaryResponse;
import com.meetmind.backend.dto.TranscriptRequest;
import com.meetmind.backend.dto.TranscriptResponse;
import com.meetmind.backend.entity.Meeting;
import com.meetmind.backend.entity.Transcript;
import com.meetmind.backend.service.MeetingProcessingService;
import com.meetmind.backend.service.MeetingService;
import com.meetmind.backend.service.TranscriptService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/meetings/{meetingId}")
@RequiredArgsConstructor
public class TranscriptController {

    private final MeetingService meetingService;
    private final TranscriptService transcriptService;
    private final MeetingProcessingService meetingProcessingService;

    @PostMapping("/transcript")
    public ResponseEntity<TranscriptResponse> saveTextTranscript(
            @PathVariable Long meetingId,
            @Valid @RequestBody TranscriptRequest request) {
        Meeting meeting = meetingService.findOwnedMeeting(meetingId);
        Transcript transcript = transcriptService.saveTextTranscript(meeting, request.getContent());
        return ResponseEntity.ok(TranscriptResponse.fromEntity(transcript));
    }

    @PostMapping(value = "/audio", consumes = "multipart/form-data")
    public ResponseEntity<TranscriptResponse> saveAudioTranscript(
            @PathVariable Long meetingId,
            @RequestParam("file") MultipartFile file) {
        Meeting meeting = meetingService.findOwnedMeeting(meetingId);
        Transcript transcript = transcriptService.saveAudioTranscript(meeting, file);
        return ResponseEntity.ok(TranscriptResponse.fromEntity(transcript));
    }

    @PostMapping("/process")
    public ResponseEntity<SummaryResponse> processMeeting(@PathVariable Long meetingId) {
        return ResponseEntity.ok(meetingProcessingService.processMeeting(meetingId));
    }

    @GetMapping("/summary")
    public ResponseEntity<SummaryResponse> getSummary(@PathVariable Long meetingId) {
        return ResponseEntity.ok(meetingProcessingService.getSummary(meetingId));
    }
}
