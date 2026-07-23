package com.meetmind.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class DashboardResponse {
    private long totalMeetings;
    private long totalTasks;
    private long pendingTasks;
    private long completedTasks;
    private List<ActionItemResponse> upcomingDeadlines;
    private List<MeetingResponse> recentMeetings;
}
