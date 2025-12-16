package com.bookhup.dto.response.book;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadingProgressDTO {

    private Long progressId;
    private Long userId;
    private String userName;

    private String readingStatus;
    private Integer currentPage;
    private Integer totalPages;
    private Float percentDone;
    private Float avgReadSpeed;
    private String lastDevice;
    private Float focusScore;

    private LocalDateTime startDate;
    private LocalDateTime finishedDate;
    private LocalDateTime lastUpdated;
}

