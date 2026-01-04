package com.bookhup.dto.response.ai.recommendation;

import com.bookhup.dto.response.book.GenreDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecommendationDTO {
    private Long bookId;
    private String title;
    private String authorName;
    private Set<GenreDTO> genres;
    private Float avgRating;
    private String coverUrl;
    private Long viewCount;
    private Long readCount;
    private Double trendScore;
    private Float confidenceScore;
    private String reason;
}
