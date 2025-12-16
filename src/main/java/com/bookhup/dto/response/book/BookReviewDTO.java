package com.bookhup.dto.response.book;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookReviewDTO {

    private Long reviewId;
    private Integer rating;
    private String comment;
    private String imageUrl;
    private String lang;
    private Integer likesCount;
    private Float aiSentimentScore;
    private String translatedText;
    private LocalDateTime createdAt;

    private Long userId;
    private String userName;
}
