package com.bookhup.dto.request.readingProgress;

import lombok.Data;

@Data
public class ReadingUpdateRequest {
    private Long bookId;
    private Integer currentPage;
    private String device;
}
