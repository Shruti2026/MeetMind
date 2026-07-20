package com.meetmind.backend.exception;

public class TranscriptNotFoundException extends RuntimeException {
    public TranscriptNotFoundException() {
        super("No transcript found for this meeting. Add a transcript or upload audio first.");
    }
}
