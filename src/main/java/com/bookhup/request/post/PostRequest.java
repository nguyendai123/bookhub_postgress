package com.bookhup.request.post;

import lombok.Data;

import java.util.List;

@Data
public class PostRequest {
    private String content;
    private String translatedText;
    private String imageUrl;
    private List<String> hashtags;
    private Long bookId;
    private Long shareOf;
}

