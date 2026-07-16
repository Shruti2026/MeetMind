package com.meetmind.backend.repository;

import com.meetmind.backend.entity.Meeting;
import com.meetmind.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    List<Meeting> findByUserOrderByCreatedAtDesc(User user);
}
