package com.meetmind.backend.dto;

import com.meetmind.backend.entity.ActionItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
public class ActionItemResponse {
    private Long id;
    private String description;
    private String assignee;
    private LocalDate dueDate;
    private ActionItem.TaskStatus status;

    public static ActionItemResponse fromEntity(ActionItem actionItem) {
        return ActionItemResponse.builder()
                .id(actionItem.getId())
                .description(actionItem.getDescription())
                .assignee(actionItem.getAssignee())
                .dueDate(actionItem.getDueDate())
                .status(actionItem.getStatus())
                .build();
    }
}
