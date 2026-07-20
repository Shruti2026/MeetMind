package com.meetmind.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MeetingRequest {

    @NotBlank
    private String title;

    private LocalDateTime meetingDate;

    private String participants;
}
