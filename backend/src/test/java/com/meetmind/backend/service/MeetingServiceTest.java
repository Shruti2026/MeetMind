package com.meetmind.backend.service;

import com.meetmind.backend.config.CurrentUserProvider;
import com.meetmind.backend.dto.MeetingRequest;
import com.meetmind.backend.dto.MeetingResponse;
import com.meetmind.backend.entity.Meeting;
import com.meetmind.backend.entity.User;
import com.meetmind.backend.exception.MeetingNotFoundException;
import com.meetmind.backend.repository.MeetingRepository;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MeetingServiceTest {

    @Mock
    private MeetingRepository meetingRepository;

    @Mock
    private CurrentUserProvider currentUserProvider;

    @InjectMocks
    private MeetingService meetingService;

    private User owner() {
        return User.builder().id(1L).name("Jane").email("jane@example.com").password("x").build();
    }

    @Test
    void createMeeting_savesMeetingForCurrentUser() {
        User user = owner();
        when(currentUserProvider.getCurrentUser()).thenReturn(user);

        MeetingRequest request = new MeetingRequest();
        request.setTitle("Sprint Planning");

        Meeting saved = Meeting.builder().id(10L).title("Sprint Planning").user(user)
                .status(Meeting.MeetingStatus.PENDING).build();
        when(meetingRepository.save(any(Meeting.class))).thenReturn(saved);

        MeetingResponse response = meetingService.createMeeting(request);

        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getTitle()).isEqualTo("Sprint Planning");
    }

    @Test
    void getMeeting_throwsWhenNotFound() {
        when(meetingRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> meetingService.getMeeting(99L))
                .isInstanceOf(MeetingNotFoundException.class);
    }

    @Test
    void getMeeting_throwsWhenNotOwnedByCurrentUser() {
        User owner = owner();
        User otherUser = User.builder().id(2L).name("Bob").email("bob@example.com").password("x").build();

        Meeting meeting = Meeting.builder().id(5L).title("Standup").user(owner).build();
        when(meetingRepository.findById(5L)).thenReturn(Optional.of(meeting));
        when(currentUserProvider.getCurrentUser()).thenReturn(otherUser);

        assertThatThrownBy(() -> meetingService.getMeeting(5L))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void getMeeting_returnsMeetingWhenOwnedByCurrentUser() {
        User user = owner();
        Meeting meeting = Meeting.builder().id(5L).title("Standup").user(user).build();
        when(meetingRepository.findById(5L)).thenReturn(Optional.of(meeting));
        when(currentUserProvider.getCurrentUser()).thenReturn(user);

        MeetingResponse response = meetingService.getMeeting(5L);

        assertThat(response.getTitle()).isEqualTo("Standup");
    }
}
