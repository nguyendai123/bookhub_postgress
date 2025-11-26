package com.bookhup.dto.request.share;

import lombok.Data;

import java.util.List;

@Data
public class ShareRequest {
    private Long postId;       // ID bài viết gốc
    private String content;    // Nội dung chia sẻ thêm
    private String translatedText;
    private String imageUrl;
    private List<String> hashtags;
}

