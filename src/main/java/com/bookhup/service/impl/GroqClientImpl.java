package com.bookhup.service.impl;

import com.bookhup.dto.request.ai.aiInteraction.AIAskRequest;
import com.bookhup.dto.response.ai.aiInteraction.AIAnswerResponse;
import com.bookhup.dto.response.ai.highLight.AIHighlightResponse;
import com.bookhup.service.GroqClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GroqClientImpl implements GroqClient {


    private final RestTemplate restTemplate;

    @Value("${groq.api-key}")
    private String apiKey;

    private static final String GROQ_URL =
            "https://api.groq.com/openai/v1/chat/completions";

    @Override
    public AIHighlightResponse highlight(String text) {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String prompt = """
                You are an assistant for analyzing highlighted text from a book.

                Given a highlighted passage, return:
                1. Sentiment: POSITIVE, NEGATIVE, or NEUTRAL
                2. A very short summary (1 sentence, neutral tone)
                3. 3–5 important keywords

                Only return valid JSON.

                Highlighted text:
                \"%s\"
                """.formatted(text);

        Map<String, Object> body = Map.of(
                "model", "llama-3.3-70b-versatile",
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)
                ),
                "temperature", 0.2
        );

        HttpEntity<?> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map<String, Object>> res =
                restTemplate.exchange(
                        GROQ_URL,
                        HttpMethod.POST,
                        entity,
                        new ParameterizedTypeReference<Map<String, Object>>() {
                        }
                );

        Map choice = (Map) ((List) res.getBody().get("choices")).get(0);
        Map msg = (Map) choice.get("message");
        String content = (String) msg.get("content");
        // Parse JSON AI trả về
        try {
            return new ObjectMapper().readValue(content, AIHighlightResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public AIAnswerResponse ask(AIAskRequest req) {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String prompt = """
                Sách: %s

                Tóm tắt:
                %s

                Highlight:
                %s
                Câu hỏi:
                %s
                """.formatted(
                req.getContext().getBookTitle(),
                req.getContext().getSummaries(),
                req.getContext().getHighlights(),
                req.getQuestion()
        );

        Map<String, Object> body = Map.of(
                "model", "llama-3.1-8b-instant",
                "messages", List.of(
                        Map.of("role", "system", "content", "Bạn là trợ lý đọc sách."),
                        Map.of("role", "user", "content", prompt)
                ),
                "temperature", 0.4
        );

        HttpEntity<?> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map<String, Object>> res =
                restTemplate.exchange(
                        GROQ_URL,
                        HttpMethod.POST,
                        entity,
                        new ParameterizedTypeReference<Map<String, Object>>() {
                        }
                );


        Map choice = (Map) ((List) res.getBody().get("choices")).get(0);
        Map msg = (Map) choice.get("message");

        return AIAnswerResponse.builder()
                .answer((String) msg.get("content"))
                .confidence(0.92f)
                .model("llama3-groq")
                .build();
    }

    @Override
    public List<String> generateHighlights(String chapterText) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String prompt = """
                Extract 5–10 important sentences from the following book chapter.
                Focus on key ideas, themes, and memorable lines.

                Return ONLY a JSON array of strings.

                Text:
                \"%s\"
                """.formatted(chapterText);

        Map<String, Object> body = Map.of(
                "model", "llama-3.3-70b-versatile",
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)
                ),
                "temperature", 0.3
        );

        HttpEntity<?> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map<String, Object>> res =
                restTemplate.exchange(
                        GROQ_URL,
                        HttpMethod.POST,
                        entity,
                        new ParameterizedTypeReference<Map<String, Object>>() {
                        }
                );

        Map choice = (Map) ((List) res.getBody().get("choices")).get(0);
        Map msg = (Map) choice.get("message");
        String content = (String) msg.get("content");
        // Parse JSON AI trả về
        try {
            return new ObjectMapper().readValue(content, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
