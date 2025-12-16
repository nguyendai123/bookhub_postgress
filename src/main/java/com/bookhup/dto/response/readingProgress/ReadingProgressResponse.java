package com.bookhup.dto.response.readingProgress;

import com.bookhup.model.ReadingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class ReadingProgressResponse {
    private Long bookId;
    private ReadingStatus readingStatus;
    private Integer currentPage;
    private Float percentDone;
    private LocalDateTime lastUpdated;
}

