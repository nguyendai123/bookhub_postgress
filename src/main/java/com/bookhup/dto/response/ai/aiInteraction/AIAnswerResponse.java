package com.bookhup.dto.response.ai.aiInteraction;

import lombok.Data;

@Data
public class AIAnswerResponse {
    private String answer;
    private Float confidence;
    private String model;
}

