package com.bookhup.service.impl;

import com.bookhup.dto.request.ai.aiInteraction.AIAskRequest;
import com.bookhup.dto.request.ai.recommendation.AIRecommendationRequest;
import com.bookhup.dto.response.ai.aiInteraction.AIAnswerResponse;
import com.bookhup.dto.response.ai.highLight.AIHighlightResponse;
import com.bookhup.dto.response.ai.recommendation.AIRecommendationResponse;
import com.bookhup.dto.response.ai.summary.AISummaryAIResult;
import com.bookhup.service.AIClient;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OpenAIClientImpl implements AIClient {
    private static final Logger log = LoggerFactory.getLogger(OpenAIClientImpl.class);

    private final RestTemplate restTemplate;

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url}")
    private String aiEngineUrl;

    @Override
    public AISummaryAIResult summarize(String content, String lang) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> payload = Map.of(
                "model", "gpt-4.1",
                "temperature", 0.3,
                "messages", List.of(
                        Map.of("role", "system", "content",
                                "Bạn là AI chuyên tóm tắt sách"),
                        Map.of("role", "user", "content",
                                "Hãy tóm tắt bằng " + lang + ":\n" + content)
                )
        );

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(payload, headers);
        try {
            ResponseEntity<Map<String, Object>> response =
                    restTemplate.exchange(
                            aiEngineUrl + "/api/summarize",
                            HttpMethod.POST,
                            request,
                            new ParameterizedTypeReference<Map<String, Object>>() {
                            }
                    );

            // ✅ parse thành object domain
            return parseResponse(response.getBody());

        } catch (HttpClientErrorException e) {
            log.error("AI 4xx error: {}", e.getResponseBodyAsString());
            return AISummaryAIResult.error("CLIENT_ERROR", e.getResponseBodyAsString());

        } catch (HttpServerErrorException e) {
            log.error("AI 5xx error: {}", e.getResponseBodyAsString());
            return AISummaryAIResult.error("SERVER_ERROR", "AI server error");

        } catch (ResourceAccessException e) {
            log.error("AI timeout", e);
            return AISummaryAIResult.error("TIMEOUT", "AI request timeout");
        }
    }

    private AISummaryAIResult parseResponse(Map body) {

        List choices = (List) body.get("choices");
        Map choice = (Map) choices.get(0);
        Map message = (Map) choice.get("message");

        String summary = (String) message.get("content");

        return AISummaryAIResult.builder()
                .summary(summary)
                .confidence(0.95f)
                .modelVersion("gpt-4.1")
                .build();
    }

    @Override
    public AIRecommendationResponse recommend(AIRecommendationRequest req) {
        return restTemplate.postForObject(
                aiEngineUrl + "/api/recommend",
                req,
                AIRecommendationResponse.class
        );
    }

    @Override
    public AIHighlightResponse highlight(String text) {
        return restTemplate.postForObject(
                aiEngineUrl + "/api/highlight",
                Map.of("text", text),
                AIHighlightResponse.class
        );
    }

    @Override
    public AIAnswerResponse ask(AIAskRequest req, Long bookId) {
        // 6️⃣ Call AI Engine
        try {
            ResponseEntity<AIAnswerResponse> res =
                    restTemplate.postForEntity(
                            aiEngineUrl + "/api/ask",
                            req,
                            AIAnswerResponse.class
                    );

            return res.getBody();

        } catch (Exception e) {
            log.error("❌ AI ask failed", e);

            // fallback an toàn
            AIAnswerResponse fallback = new AIAnswerResponse();
            fallback.setAnswer("Xin lỗi, AI chưa thể trả lời lúc này.");
            fallback.setConfidence(0.0f);
            fallback.setModel("fallback");

            return fallback;
        }
    }

}
