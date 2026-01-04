package com.bookhup.service.impl;

import com.bookhup.dto.response.ai.summary.AISummaryAIResult;
import com.bookhup.service.HuggingFaceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class HuggingFaceClientImpl implements HuggingFaceClient {

    private final RestTemplate restTemplate;

    @Value("${huggingface.token}")
    private String hfToken;
    private static final String SUMMARY_URL =
            "https://router.huggingface.co/hf-inference/models/facebook/bart-large-cnn";

    @Override
    public AISummaryAIResult summarize(
            String content,
            String bookTitle,
            String author,
            String scope,
            String lang
    ) {

        // 1. Split content into chunks (BART-safe)
        List<String> chunks = splitText(content, 1000);

        List<String> partialSummaries = new ArrayList<>();

        for (String chunk : chunks) {
            String chunkSummary = summarizeChunk(
                    chunk,
                    bookTitle,
                    author,
                    scope
            );
            partialSummaries.add(chunkSummary);
        }

        // 2. Merge partial summaries
        String mergedSummary = String.join("\n", partialSummaries);

        // 3. Final summarization pass
        String finalSummary = summarizeMerged(
                mergedSummary,
                bookTitle,
                author,
                scope
        );

        return AISummaryAIResult.builder()
                .summary(finalSummary)
                .confidence(0.9f)
                .modelVersion("facebook/bart-large-cnn")
                .build();
    }
    private String summarizeChunk(
            String chunk,
            String bookTitle,
            String author,
            String scope
    ) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(hfToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        String prompt = String.format(
                """
                Please write a clear and neutral summary in English,
                similar to professional book summaries found on the internet.
                
    
                Book:
                - Title: %s
                - Author: %s
    
                Content:
                %s
                """,
                bookTitle,
                author,
                chunk
        );

        Map<String, Object> body = Map.of(
                "inputs", prompt,
                "parameters", Map.of(
                        "max_length", 250,
                        "do_sample", false
                )
        );

        HttpEntity<?> req = new HttpEntity<>(body, headers);

        ResponseEntity<List<Map<String, Object>>> res =
                restTemplate.exchange(
                        SUMMARY_URL,
                        HttpMethod.POST,
                        req,
                        new ParameterizedTypeReference<>() {}
                );

        return (String) res.getBody().get(0).get("summary_text");
    }
    private String summarizeMerged(
            String merged,
            String bookTitle,
            String author,
            String scope
    ) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(hfToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        String prompt = String.format(
                """
               Please write a clear and neutral summary in English %s summary,
               similar to professional book summaries found on the internet.
    
                Book:
                - Title: %s
                - Author: %s
    
                Summaries:
                %s
                """,
                scope.equals("CHAPTER") ? "chapter" : "book",
                bookTitle,
                author,
                merged
        );

        Map<String, Object> body = Map.of(
                "inputs", prompt,
                "parameters", Map.of(
                        "max_length", scope.equals("BOOK") ? 1200 : 500,
                        "min_length", scope.equals("BOOK") ? 600 : 250,
                        "do_sample", false
                )
        );

        HttpEntity<?> req = new HttpEntity<>(body, headers);

        ResponseEntity<List<Map<String, Object>>> res =
                restTemplate.exchange(
                        SUMMARY_URL,
                        HttpMethod.POST,
                        req,
                        new ParameterizedTypeReference<>() {}
                );

        return (String) res.getBody().get(0).get("summary_text");
    }

    private List<String> splitText(String text, int maxChars) {
        List<String> chunks = new ArrayList<>();
        int start = 0;

        while (start < text.length()) {
            int end = Math.min(start + maxChars, text.length());
            chunks.add(text.substring(start, end));
            start = end;
        }
        return chunks;
    }

}
