package com.meetmind.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GeminiAnalysisResult {
    private String summary;
    private String keyDecisions;
    private List<ActionItemPayload> actionItems;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActionItemPayload {
        private String description;
        private String assignee;
        private String dueDate;
    }
}
