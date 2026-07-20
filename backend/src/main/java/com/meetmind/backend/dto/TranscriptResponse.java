package com.meetmind.backend.dto;

import com.meetmind.backend.entity.Transcript;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TranscriptResponse {
    private Long id;
    private String content;
    private Transcript.SourceType sourceType;

    public static TranscriptResponse fromEntity(Transcript transcript) {
        return TranscriptResponse.builder()
                .id(transcript.getId())
                .content(transcript.getContent())
                .sourceType(transcript.getSourceType())
                .build();
    }
}
