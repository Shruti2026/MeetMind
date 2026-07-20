package com.meetmind.backend.exception;

public class SummaryNotFoundException extends RuntimeException {
    public SummaryNotFoundException() {
        super("This meeting has not been processed yet");
    }
}
