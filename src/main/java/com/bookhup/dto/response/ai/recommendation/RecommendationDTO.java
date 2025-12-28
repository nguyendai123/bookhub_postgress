package com.bookhup.dto.response.ai.recommendation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecommendationDTO {
    private Long bookId;
    private String title;
    private Float confidenceScore;
    private String reason;
}
