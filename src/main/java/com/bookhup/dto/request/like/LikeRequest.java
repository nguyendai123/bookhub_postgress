package com.bookhup.dto.request.like;

import lombok.Data;

@Data
public class LikeRequest {
    private Long postId;
    private String targetType; // POST, COMMENT, REVIEW
    private Long targetId;     // ID của đối tượng
}

