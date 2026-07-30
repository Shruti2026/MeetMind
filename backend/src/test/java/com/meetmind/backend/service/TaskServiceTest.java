package com.meetmind.backend.service;

import com.meetmind.backend.config.CurrentUserProvider;
import com.meetmind.backend.dto.ActionItemResponse;
import com.meetmind.backend.entity.ActionItem;
import com.meetmind.backend.entity.Meeting;
import com.meetmind.backend.entity.Summary;
import com.meetmind.backend.entity.User;
import com.meetmind.backend.exception.TaskNotFoundException;
import com.meetmind.backend.repository.ActionItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private ActionItemRepository actionItemRepository;

    @Mock
    private CurrentUserProvider currentUserProvider;

    @InjectMocks
    private TaskService taskService;

    private ActionItem taskOwnedBy(User user) {
        Meeting meeting = Meeting.builder().id(1L).title("Standup").user(user).build();
        Summary summary = Summary.builder().id(1L).summaryText("...").meeting(meeting).build();
        return ActionItem.builder()
                .id(7L)
                .description("Send follow-up email")
                .status(ActionItem.TaskStatus.PENDING)
                .summary(summary)
                .build();
    }

    @Test
    void updateStatus_marksTaskCompletedWhenOwnedByCurrentUser() {
        User user = User.builder().id(1L).name("Jane").email("jane@example.com").password("x").build();
        ActionItem task = taskOwnedBy(user);

        when(actionItemRepository.findById(7L)).thenReturn(Optional.of(task));
        when(currentUserProvider.getCurrentUser()).thenReturn(user);
        when(actionItemRepository.save(any(ActionItem.class))).thenAnswer(inv -> inv.getArgument(0));

        ActionItemResponse response = taskService.updateStatus(7L, ActionItem.TaskStatus.COMPLETED);

        assertThat(response.getStatus()).isEqualTo(ActionItem.TaskStatus.COMPLETED);
    }

    @Test
    void updateStatus_throwsWhenTaskNotOwnedByCurrentUser() {
        User owner = User.builder().id(1L).name("Jane").email("jane@example.com").password("x").build();
        User otherUser = User.builder().id(2L).name("Bob").email("bob@example.com").password("x").build();
        ActionItem task = taskOwnedBy(owner);

        when(actionItemRepository.findById(7L)).thenReturn(Optional.of(task));
        when(currentUserProvider.getCurrentUser()).thenReturn(otherUser);

        assertThatThrownBy(() -> taskService.updateStatus(7L, ActionItem.TaskStatus.COMPLETED))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void deleteTask_throwsWhenTaskDoesNotExist() {
        when(actionItemRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.deleteTask(404L))
                .isInstanceOf(TaskNotFoundException.class);
    }

    @Test
    void deleteTask_removesTaskWhenOwnedByCurrentUser() {
        User user = User.builder().id(1L).name("Jane").email("jane@example.com").password("x").build();
        ActionItem task = taskOwnedBy(user);

        when(actionItemRepository.findById(7L)).thenReturn(Optional.of(task));
        when(currentUserProvider.getCurrentUser()).thenReturn(user);

        taskService.deleteTask(7L);

        verify(actionItemRepository).delete(task);
    }
}
