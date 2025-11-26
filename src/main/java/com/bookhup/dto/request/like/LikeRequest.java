package com.bookhup.dto.request.like;

import lombok.Data;

@Data
public class LikeRequest {
    private String targetType; // POST, COMMENT, REVIEW
    private Long targetId;     // ID của đối tượng
}

