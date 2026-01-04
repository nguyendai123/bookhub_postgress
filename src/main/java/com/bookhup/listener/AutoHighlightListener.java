package com.bookhup.listener;

import com.bookhup.event.AutoHighlightChapterEvent;
import com.bookhup.repository.UserFeedWeightsRepository;
import com.bookhup.service.HighlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AutoHighlightListener {
    private final HighlightService aiHighlightService;

    @Async
    @EventListener
    public void handle(AutoHighlightChapterEvent event) {
//        aiHighlightService.autoHighlightChapter(event.getChapterId());
    }
}

