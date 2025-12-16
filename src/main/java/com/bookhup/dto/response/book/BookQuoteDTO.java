package com.bookhup.dto.response.book;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookQuoteDTO {

    private Long quoteId;
    private String quoteText;
    private String sourceChapter;
    private Boolean aiGenerated;
    private Integer popularityScore;

    private Long addedByUserId;
}

