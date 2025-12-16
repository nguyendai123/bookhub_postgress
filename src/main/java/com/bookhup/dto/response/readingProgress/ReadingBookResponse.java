package com.bookhup.dto.response.readingProgress;

import com.bookhup.dto.response.book.GenreDTO;
import lombok.*;
import com.bookhup.model.ReadingStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReadingBookResponse {

    // ===== Book info =====
    private Long bookId;
    private String title;
    private String coverUrl;

    private String authorName;

    private Float avgRating;
    private Integer totalReviews;
    private Integer totalPages;

    private List<GenreDTO> genres;

    // ===== Reading progress =====
    private ReadingStatus readingStatus;
    private Integer currentPage;
    private Float percentDone;
    private LocalDateTime lastUpdated;
}

