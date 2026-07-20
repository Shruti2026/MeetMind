package com.meetmind.backend.exception;

public class MeetingNotFoundException extends RuntimeException {
    public MeetingNotFoundException(Long id) {
        super("Meeting not found with id: " + id);
    }
}
