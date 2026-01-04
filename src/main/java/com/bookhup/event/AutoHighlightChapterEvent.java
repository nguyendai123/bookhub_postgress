package com.bookhup.event;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class AutoHighlightChapterEvent {

    private final Long chapterId;
    private final String trigger; // READ_COMPLETE, CRON, ADMIN, ...

    public AutoHighlightChapterEvent(Long chapterId) {
        this.chapterId = chapterId;
        this.trigger = "READ_COMPLETE";
    }

    public AutoHighlightChapterEvent(Long chapterId, String trigger) {
        this.chapterId = chapterId;
        this.trigger = trigger;
    }
}

