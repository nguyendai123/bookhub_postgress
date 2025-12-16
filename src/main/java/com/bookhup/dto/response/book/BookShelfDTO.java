package com.bookhup.dto.response.book;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookShelfDTO {

    private Long bookId;

    private String title;

    private String isbn;

    private String coverUrl;

    private String authorName;

    private Float avgRating;

    private Integer totalReviews;

    private Integer totalPages;

    // reading progress (nullable nếu user chưa đọc)
    private String readingStatus;   // READING | FINISHED | WANT_TO_READ
    private Integer currentPage;
    private Float percentDone;

    // genres gộp lại thành 1 list
    private List<GenreDTO> genres;

}

