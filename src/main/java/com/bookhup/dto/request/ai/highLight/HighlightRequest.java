package com.bookhup.dto.request.ai.highLight;

import lombok.Data;

@Data
public class HighlightRequest {
    private Long bookId;
    private Long chapterId;
    private String text;
    private String pageNumber;
    private String position;
    private String source;
}

