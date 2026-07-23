package com.meetmind.backend.dto;

import com.meetmind.backend.entity.ActionItem;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TaskRequest {

    @NotBlank
    private String description;

    private String assignee;

    private LocalDate dueDate;

    private ActionItem.TaskStatus status;
}
