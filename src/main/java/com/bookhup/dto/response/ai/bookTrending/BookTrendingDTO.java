package com.bookhup.dto.response.ai.bookTrending;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookTrendingDTO {

    private Long bookId;
    private String title;
    private String coverUrl;
    private Long viewCount;
    private Long readCount;
    private Double trendScore;
}

