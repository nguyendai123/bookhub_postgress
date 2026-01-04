package com.bookhup.dto.response.ai.bookTrending;

import com.bookhup.dto.response.book.GenreDTO;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class BookTrendingDTO {

    private Long bookId;
    private String title;
    private String authorName;
    private Set<GenreDTO> genres;
    private Float avgRating;
    private String coverUrl;
    private Long viewCount;
    private Long readCount;
    private Double trendScore;
}

