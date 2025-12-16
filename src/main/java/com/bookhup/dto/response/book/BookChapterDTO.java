package com.bookhup.dto.response.book;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookChapterDTO {

    private Long chapterId;
    private Integer chapterOrder;
    private String chapterTitle;
    private String textContent;
    private String audioUrl;
    private Float duration;
}

