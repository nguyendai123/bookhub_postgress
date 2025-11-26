package com.bookhup.dto.request.hashtag;

import lombok.Data;

import java.util.List;

@Data
public class PostHashtagRequest {
    private Long postId;
    private List<String> hashtags;
}

