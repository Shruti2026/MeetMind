package com.meetmind.backend.dto;

import com.meetmind.backend.entity.Meeting;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class MeetingResponse {
    private Long id;
    private String title;
    private LocalDateTime meetingDate;
    private String participants;
    private Meeting.MeetingStatus status;
    private Instant createdAt;

    public static MeetingResponse fromEntity(Meeting meeting) {
        return MeetingResponse.builder()
                .id(meeting.getId())
                .title(meeting.getTitle())
                .meetingDate(meeting.getMeetingDate())
                .participants(meeting.getParticipants())
                .status(meeting.getStatus())
                .createdAt(meeting.getCreatedAt())
                .build();
    }
}
