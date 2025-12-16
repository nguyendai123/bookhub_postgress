package com.bookhup.dto.response.book;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDetailDTO {

    private Long bookId;
    private String isbn;
    private String title;
    private String language;
    private String description;
    private String coverUrl;

    private Float avgRating;
    private Integer totalReviews;
    private Integer totalPages;

    private LocalDateTime createdAt;

    // ===== relations =====
    private AuthorDTO author;
    private Set<GenreDTO> genres;
    private Set<BookReviewDTO> bookReviews;
    private Set<BookChapterDTO> chapters;
    private Set<BookMediaAssetDTO> mediaAssets;
    private Set<BookHighlightDTO> highlights;
    private Set<BookQuoteDTO> quotes;
    private Set<ReadingProgressDTO> readingProgresses;
}

