package com.meetmind.backend.service;

import com.meetmind.backend.config.CurrentUserProvider;
import com.meetmind.backend.dto.ActionItemResponse;
import com.meetmind.backend.dto.TaskRequest;
import com.meetmind.backend.entity.ActionItem;
import com.meetmind.backend.entity.User;
import com.meetmind.backend.exception.TaskNotFoundException;
import com.meetmind.backend.repository.ActionItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final ActionItemRepository actionItemRepository;
    private final CurrentUserProvider currentUserProvider;

    public List<ActionItemResponse> getTasks() {
        User user = currentUserProvider.getCurrentUser();
        return actionItemRepository.findAllByUser(user).stream()
                .map(ActionItemResponse::fromEntity)
                .toList();
    }

    public ActionItemResponse updateTask(Long id, TaskRequest request) {
        ActionItem task = findOwnedTask(id);
        task.setDescription(request.getDescription());
        task.setAssignee(request.getAssignee());
        task.setDueDate(request.getDueDate());
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }
        return ActionItemResponse.fromEntity(actionItemRepository.save(task));
    }

    public ActionItemResponse updateStatus(Long id, ActionItem.TaskStatus status) {
        ActionItem task = findOwnedTask(id);
        task.setStatus(status);
        return ActionItemResponse.fromEntity(actionItemRepository.save(task));
    }

    public void deleteTask(Long id) {
        ActionItem task = findOwnedTask(id);
        actionItemRepository.delete(task);
    }

    private ActionItem findOwnedTask(Long id) {
        ActionItem task = actionItemRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));

        User currentUser = currentUserProvider.getCurrentUser();
        User owner = task.getSummary().getMeeting().getUser();
        if (!owner.getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You do not have access to this task");
        }

        return task;
    }
}
