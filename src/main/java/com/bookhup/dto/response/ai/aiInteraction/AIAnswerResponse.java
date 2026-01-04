package com.bookhup.dto.response.ai.aiInteraction;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AIAnswerResponse {
    private String answer;
    private Float confidence;
    private String model;
}

