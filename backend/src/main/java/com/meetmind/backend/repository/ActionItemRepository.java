package com.meetmind.backend.repository;

import com.meetmind.backend.entity.ActionItem;
import com.meetmind.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ActionItemRepository extends JpaRepository<ActionItem, Long> {

    @Query("SELECT a FROM ActionItem a WHERE a.summary.meeting.user = :user ORDER BY a.createdAt DESC")
    List<ActionItem> findAllByUser(@Param("user") User user);

    @Query("SELECT a FROM ActionItem a WHERE a.summary.meeting.user = :user AND a.status = :status ORDER BY a.dueDate ASC NULLS LAST")
    List<ActionItem> findAllByUserAndStatus(@Param("user") User user, @Param("status") ActionItem.TaskStatus status);
}
