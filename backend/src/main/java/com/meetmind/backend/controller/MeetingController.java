package com.meetmind.backend.controller;

import com.meetmind.backend.dto.MeetingRequest;
import com.meetmind.backend.dto.MeetingResponse;
import com.meetmind.backend.service.MeetingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/meetings")
@RequiredArgsConstructor
public class MeetingController {

    private final MeetingService meetingService;

    @PostMapping
    public ResponseEntity<MeetingResponse> createMeeting(@Valid @RequestBody MeetingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(meetingService.createMeeting(request));
    }

    @GetMapping
    public ResponseEntity<List<MeetingResponse>> getMeetings() {
        return ResponseEntity.ok(meetingService.getMeetings());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MeetingResponse> getMeeting(@PathVariable Long id) {
        return ResponseEntity.ok(meetingService.getMeeting(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MeetingResponse> updateMeeting(@PathVariable Long id, @Valid @RequestBody MeetingRequest request) {
        return ResponseEntity.ok(meetingService.updateMeeting(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMeeting(@PathVariable Long id) {
        meetingService.deleteMeeting(id);
        return ResponseEntity.noContent().build();
    }
}
