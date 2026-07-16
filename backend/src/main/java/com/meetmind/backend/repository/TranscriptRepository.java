package com.meetmind.backend.repository;

import com.meetmind.backend.entity.Transcript;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TranscriptRepository extends JpaRepository<Transcript, Long> {
}
