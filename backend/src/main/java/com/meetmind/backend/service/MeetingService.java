package com.meetmind.backend.service;

import com.meetmind.backend.config.CurrentUserProvider;
import com.meetmind.backend.dto.MeetingRequest;
import com.meetmind.backend.dto.MeetingResponse;
import com.meetmind.backend.entity.Meeting;
import com.meetmind.backend.entity.User;
import com.meetmind.backend.exception.MeetingNotFoundException;
import com.meetmind.backend.repository.MeetingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final CurrentUserProvider currentUserProvider;

    public MeetingResponse createMeeting(MeetingRequest request) {
        User user = currentUserProvider.getCurrentUser();

        Meeting meeting = Meeting.builder()
                .title(request.getTitle())
                .meetingDate(request.getMeetingDate())
                .participants(request.getParticipants())
                .status(Meeting.MeetingStatus.PENDING)
                .user(user)
                .build();

        Meeting saved = meetingRepository.save(meeting);
        return MeetingResponse.fromEntity(saved);
    }

    public List<MeetingResponse> getMeetings() {
        User user = currentUserProvider.getCurrentUser();
        return meetingRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .map(MeetingResponse::fromEntity)
                .toList();
    }

    public MeetingResponse getMeeting(Long id) {
        Meeting meeting = findOwnedMeeting(id);
        return MeetingResponse.fromEntity(meeting);
    }

    public MeetingResponse updateMeeting(Long id, MeetingRequest request) {
        Meeting meeting = findOwnedMeeting(id);
        meeting.setTitle(request.getTitle());
        meeting.setMeetingDate(request.getMeetingDate());
        meeting.setParticipants(request.getParticipants());
        Meeting saved = meetingRepository.save(meeting);
        return MeetingResponse.fromEntity(saved);
    }

    public void deleteMeeting(Long id) {
        Meeting meeting = findOwnedMeeting(id);
        meetingRepository.delete(meeting);
    }

    private Meeting findOwnedMeeting(Long id) {
        Meeting meeting = meetingRepository.findById(id)
                .orElseThrow(() -> new MeetingNotFoundException(id));

        User currentUser = currentUserProvider.getCurrentUser();
        if (!meeting.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You do not have access to this meeting");
        }

        return meeting;
    }
}
