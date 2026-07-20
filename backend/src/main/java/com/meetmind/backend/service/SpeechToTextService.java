package com.meetmind.backend.service;

import org.springframework.web.multipart.MultipartFile;

public interface SpeechToTextService {
    String transcribe(MultipartFile audioFile);
}
