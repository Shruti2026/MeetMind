package com.meetmind.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meetmind.backend.dto.GeminiAnalysisResult;
import com.meetmind.backend.exception.AiProcessingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    private static final String GEMINI_ENDPOINT =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent";

    @Value("${gemini.api-key:}")
    private String apiKey;

    private final RestClient restClient = RestClient.create();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public GeminiAnalysisResult analyzeTranscript(String transcript) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new AiProcessingException("Gemini API key is not configured");
        }

        String prompt = buildPrompt(transcript);

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(Map.of("text", prompt)))
                )
        );

        Map<String, Object> response = restClient.post()
                .uri(GEMINI_ENDPOINT + "?key=" + apiKey)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(Map.class);

        String rawText = extractText(response);
        return parseJsonResponse(rawText);
    }

    private String buildPrompt(String transcript) {
        return """
                You are an assistant that analyzes meeting transcripts.
                Given the transcript below, respond with ONLY a valid JSON object
                (no markdown, no code fences) in this exact shape:

                {
                  "summary": "concise meeting summary",
                  "keyDecisions": "key decisions made during the meeting",
                  "actionItems": [
                    { "description": "task description", "assignee": "name or null", "dueDate": "YYYY-MM-DD or null" }
                  ]
                }

                Transcript:
                %s
                """.formatted(transcript);
    }

    @SuppressWarnings("unchecked")
    private String extractText(Map<String, Object> response) {
        try {
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
            return (String) parts.get(0).get("text");
        } catch (Exception e) {
            throw new AiProcessingException("Failed to parse Gemini response");
        }
    }

    private GeminiAnalysisResult parseJsonResponse(String rawText) {
        try {
            String cleaned = rawText.trim()
                    .replaceAll("^```json", "")
                    .replaceAll("^```", "")
                    .replaceAll("```$", "")
                    .trim();
            JsonNode node = objectMapper.readTree(cleaned);
            return objectMapper.treeToValue(node, GeminiAnalysisResult.class);
        } catch (Exception e) {
            throw new AiProcessingException("Gemini returned an unparseable response");
        }
    }
}
