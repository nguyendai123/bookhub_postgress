package com.bookhup.dto.request.review;

import lombok.Data;

@Data
public class ReviewRequest {
    private Long bookId;
    private Integer rating;
    private String comment;
    private String lang;
}

