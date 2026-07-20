package com.meetmind.backend.service;

import com.meetmind.backend.exception.AiProcessingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class WhisperSpeechToTextService implements SpeechToTextService {

    private static final String WHISPER_ENDPOINT = "https://api.openai.com/v1/audio/transcriptions";

    @Value("${openai.api-key:}")
    private String apiKey;

    private final RestClient restClient = RestClient.create();

    @Override
    public String transcribe(MultipartFile audioFile) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new AiProcessingException("OpenAI API key is not configured for speech-to-text");
        }

        try {
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            ByteArrayResource fileResource = new ByteArrayResource(audioFile.getBytes()) {
                @Override
                public String getFilename() {
                    return audioFile.getOriginalFilename();
                }
            };
            body.add("file", fileResource);
            body.add("model", "whisper-1");

            Map<String, Object> response = restClient.post()
                    .uri(WHISPER_ENDPOINT)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(body)
                    .retrieve()
                    .body(Map.class);

            if (response == null || response.get("text") == null) {
                throw new AiProcessingException("Speech-to-text service returned an empty response");
            }

            return response.get("text").toString();
        } catch (IOException e) {
            throw new AiProcessingException("Failed to read uploaded audio file");
        }
    }
}
