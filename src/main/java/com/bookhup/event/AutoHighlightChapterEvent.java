package com.bookhup.event;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class AutoHighlightChapterEvent {

    private final Long chapterId;
    private Long triggerUserId; // user kích hoạt (để log / analytics)

    public AutoHighlightChapterEvent(Long chapterId) {
        this.chapterId = chapterId;
        this.triggerUserId = 1L;
    }

    public AutoHighlightChapterEvent(Long chapterId, Long triggerUserId) {
        this.chapterId = chapterId;
        this.triggerUserId = triggerUserId;
    }
}

