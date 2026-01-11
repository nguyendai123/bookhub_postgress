package com.bookhup.listener;

import com.bookhup.event.AutoHighlightChapterEvent;
import com.bookhup.repository.BookChapterRepository;
import com.bookhup.repository.BookHighlightRepository;
import com.bookhup.service.HighlightService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AutoHighlightChapterListener {

    private final HighlightService aiHighlightService;
    private final BookChapterRepository chapterRepo;
    private final BookHighlightRepository highlightRepo;


    @Async
    @EventListener
    public void handle(AutoHighlightChapterEvent event) {

        log.info("🤖 AI highlighting chapter {}", event.getChapterId());

        aiHighlightService.autoHighlightChapter(
                event.getChapterId()
        );
    }
}
