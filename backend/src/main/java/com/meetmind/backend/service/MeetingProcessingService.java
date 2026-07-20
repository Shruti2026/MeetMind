package com.meetmind.backend.service;

import com.meetmind.backend.dto.GeminiAnalysisResult;
import com.meetmind.backend.dto.SummaryResponse;
import com.meetmind.backend.entity.ActionItem;
import com.meetmind.backend.entity.Meeting;
import com.meetmind.backend.entity.Summary;
import com.meetmind.backend.entity.Transcript;
import com.meetmind.backend.exception.TranscriptNotFoundException;
import com.meetmind.backend.repository.MeetingRepository;
import com.meetmind.backend.repository.SummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MeetingProcessingService {

    private final MeetingService meetingService;
    private final GeminiService geminiService;
    private final SummaryRepository summaryRepository;
    private final MeetingRepository meetingRepository;

    @Transactional
    public SummaryResponse processMeeting(Long meetingId) {
        Meeting meeting = meetingService.findOwnedMeeting(meetingId);
        Transcript transcript = meeting.getTranscript();

        if (transcript == null || transcript.getContent() == null || transcript.getContent().isBlank()) {
            throw new TranscriptNotFoundException();
        }

        meeting.setStatus(Meeting.MeetingStatus.PROCESSING);
        meetingRepository.save(meeting);

        try {
            GeminiAnalysisResult result = geminiService.analyzeTranscript(transcript.getContent());
            Summary summary = persistSummary(meeting, result);

            meeting.setStatus(Meeting.MeetingStatus.PROCESSED);
            meetingRepository.save(meeting);

            return SummaryResponse.fromEntity(summary);
        } catch (RuntimeException e) {
            meeting.setStatus(Meeting.MeetingStatus.FAILED);
            meetingRepository.save(meeting);
            throw e;
        }
    }

    private Summary persistSummary(Meeting meeting, GeminiAnalysisResult result) {
        Summary summary = meeting.getSummary() != null ? meeting.getSummary() : new Summary();
        summary.setMeeting(meeting);
        summary.setSummaryText(result.getSummary());
        summary.setKeyDecisions(result.getKeyDecisions());

        List<ActionItem> actionItems = result.getActionItems() == null ? List.of() :
                result.getActionItems().stream()
                        .map(item -> ActionItem.builder()
                                .description(item.getDescription())
                                .assignee(item.getAssignee())
                                .dueDate(parseDueDate(item.getDueDate()))
                                .status(ActionItem.TaskStatus.PENDING)
                                .summary(summary)
                                .build())
                        .toList();

        summary.setActionItems(actionItems);
        return summaryRepository.save(summary);
    }

    private LocalDate parseDueDate(String dueDate) {
        if (dueDate == null || dueDate.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(dueDate);
        } catch (Exception e) {
            return null;
        }
    }
}
