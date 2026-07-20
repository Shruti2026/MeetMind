package com.meetmind.backend.dto;

import com.meetmind.backend.entity.Summary;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class SummaryResponse {
    private Long id;
    private String summaryText;
    private String keyDecisions;
    private List<ActionItemResponse> actionItems;

    public static SummaryResponse fromEntity(Summary summary) {
        return SummaryResponse.builder()
                .id(summary.getId())
                .summaryText(summary.getSummaryText())
                .keyDecisions(summary.getKeyDecisions())
                .actionItems(summary.getActionItems() == null ? List.of() :
                        summary.getActionItems().stream().map(ActionItemResponse::fromEntity).toList())
                .build();
    }
}
