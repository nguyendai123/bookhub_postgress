package com.bookhup.dto.request.ai;

import lombok.Data;

@Data
public class AISummaryRequest {
    private Long bookId;
    private Long chapterId; // nullable
    private String lang; // vi, en
    private String type; // BOOK | CHAPTER
}

