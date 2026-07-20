package com.meetmind.backend.service;

import com.meetmind.backend.entity.Meeting;
import com.meetmind.backend.entity.Transcript;
import com.meetmind.backend.repository.TranscriptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class TranscriptService {

    private final TranscriptRepository transcriptRepository;
    private final SpeechToTextService speechToTextService;

    public Transcript saveTextTranscript(Meeting meeting, String content) {
        Transcript transcript = existingOrNew(meeting);
        transcript.setContent(content);
        transcript.setSourceType(Transcript.SourceType.TEXT);
        transcript.setMeeting(meeting);
        return transcriptRepository.save(transcript);
    }

    public Transcript saveAudioTranscript(Meeting meeting, MultipartFile audioFile) {
        String content = speechToTextService.transcribe(audioFile);

        Transcript transcript = existingOrNew(meeting);
        transcript.setContent(content);
        transcript.setSourceType(Transcript.SourceType.AUDIO);
        transcript.setAudioFileUrl(audioFile.getOriginalFilename());
        transcript.setMeeting(meeting);
        return transcriptRepository.save(transcript);
    }

    private Transcript existingOrNew(Meeting meeting) {
        return meeting.getTranscript() != null ? meeting.getTranscript() : new Transcript();
    }
}
