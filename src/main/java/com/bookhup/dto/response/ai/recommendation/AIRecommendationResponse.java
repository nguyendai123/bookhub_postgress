package com.bookhup.dto.response.ai.recommendation;

import lombok.Data;

import java.util.List;

@Data
public class AIRecommendationResponse {
    private List<AIRecItem> recommendations;

    @Data
    public static class AIRecItem {
        private Long bookId;
        private Float confidence;
        private String algorithm;
    }
}

