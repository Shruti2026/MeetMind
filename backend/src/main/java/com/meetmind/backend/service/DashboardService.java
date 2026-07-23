package com.meetmind.backend.service;

import com.meetmind.backend.config.CurrentUserProvider;
import com.meetmind.backend.dto.ActionItemResponse;
import com.meetmind.backend.dto.DashboardResponse;
import com.meetmind.backend.dto.MeetingResponse;
import com.meetmind.backend.entity.ActionItem;
import com.meetmind.backend.entity.User;
import com.meetmind.backend.repository.ActionItemRepository;
import com.meetmind.backend.repository.MeetingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final MeetingRepository meetingRepository;
    private final ActionItemRepository actionItemRepository;
    private final CurrentUserProvider currentUserProvider;

    public DashboardResponse getDashboard() {
        User user = currentUserProvider.getCurrentUser();

        List<ActionItem> pending = actionItemRepository.findAllByUserAndStatus(user, ActionItem.TaskStatus.PENDING);
        List<ActionItem> completed = actionItemRepository.findAllByUserAndStatus(user, ActionItem.TaskStatus.COMPLETED);

        List<ActionItemResponse> upcomingDeadlines = pending.stream()
                .filter(item -> item.getDueDate() != null && !item.getDueDate().isBefore(LocalDate.now()))
                .sorted(Comparator.comparing(ActionItem::getDueDate))
                .limit(5)
                .map(ActionItemResponse::fromEntity)
                .toList();

        List<MeetingResponse> recentMeetings = meetingRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .limit(5)
                .map(MeetingResponse::fromEntity)
                .toList();

        return DashboardResponse.builder()
                .totalMeetings(meetingRepository.findByUserOrderByCreatedAtDesc(user).size())
                .totalTasks(pending.size() + completed.size())
                .pendingTasks(pending.size())
                .completedTasks(completed.size())
                .upcomingDeadlines(upcomingDeadlines)
                .recentMeetings(recentMeetings)
                .build();
    }
}
