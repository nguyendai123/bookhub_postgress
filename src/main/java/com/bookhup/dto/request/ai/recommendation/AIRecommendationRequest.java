package com.bookhup.dto.request.ai.recommendation;

import lombok.Data;

import java.util.List;

@Data
public class AIRecommendationRequest {
    private Long userId;
    private List<Long> historyBookIds;
    private List<String> genres;
}

