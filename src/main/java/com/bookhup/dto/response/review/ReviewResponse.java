package com.bookhup.dto.response.review;

import com.bookhup.model.BookReview;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReviewResponse {
    private Long reviewId;
    private Long bookId;
    private Long userId;
    private Integer rating;
    private String comment;
    private String imageUrl;
    private LocalDateTime createdAt;

    public static ReviewResponse from(BookReview r) {
        return ReviewResponse.builder()
                .reviewId(r.getReviewId())
                .bookId(r.getBook().getBookId())
                .userId(r.getUser().getUserId())
                .rating(r.getRating())
                .comment(r.getComment())
                .imageUrl(r.getImageUrl())
                .createdAt(r.getCreatedAt())
                .build();
    }
}

