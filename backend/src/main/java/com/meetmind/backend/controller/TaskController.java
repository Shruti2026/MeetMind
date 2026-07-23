package com.meetmind.backend.controller;

import com.meetmind.backend.dto.ActionItemResponse;
import com.meetmind.backend.dto.TaskRequest;
import com.meetmind.backend.entity.ActionItem;
import com.meetmind.backend.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public ResponseEntity<List<ActionItemResponse>> getTasks() {
        return ResponseEntity.ok(taskService.getTasks());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ActionItemResponse> updateTask(@PathVariable Long id, @Valid @RequestBody TaskRequest request) {
        return ResponseEntity.ok(taskService.updateTask(id, request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ActionItemResponse> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        ActionItem.TaskStatus status = ActionItem.TaskStatus.valueOf(body.get("status"));
        return ResponseEntity.ok(taskService.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
