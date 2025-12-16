package com.bookhup.dto.response.book;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookHighlightDTO {

    private Long highlightId;
    private Long userId;
    private Long chapterId;

    private String text;
    private String position;
    private String sentiment;
    private String aiSummary;
    private List<String> keywords;
}

