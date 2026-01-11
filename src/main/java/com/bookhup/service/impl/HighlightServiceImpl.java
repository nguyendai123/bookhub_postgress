package com.bookhup.service.impl;

import com.bookhup.dto.request.ai.highLight.HighlightRequest;
import com.bookhup.dto.response.ai.highLight.AIHighlightResponse;
import com.bookhup.event.AutoHighlightChapterEvent;
import com.bookhup.model.*;
import com.bookhup.repository.BookChapterRepository;
import com.bookhup.repository.BookHighlightRepository;
import com.bookhup.repository.BookRepository;
import com.bookhup.service.AIClient;
import com.bookhup.service.HighlightService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class HighlightServiceImpl implements HighlightService {

    private final AIClient aiClient;
    private final BookHighlightRepository repo;
    private final BookRepository bookRepo;
    private final BookChapterRepository chapterRepo;
    private final BookHighlightRepository highlightRepo;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Highlight chương đang đọc
     */
    @Override
    public void tryHighlightCurrentChapter(ReadingProgress progress) {

        Optional<BookChapter> chapter = chapterRepo.findCurrentChapter(
                progress.getBook().getBookId(),
                progress.getCurrentPage()
        );

        if (chapter.isEmpty()) return;

        if (!highlightRepo.existsByChapter_ChapterIdAndSource(
                chapter.get().getChapterId(), "AI")) {

            eventPublisher.publishEvent(
                    new AutoHighlightChapterEvent(
                            chapter.get().getChapterId(),
                            progress.getUser().getUserId()
                    )
            );
        }
    }

    /**
     * Pre-highlight chương tiếp theo
     */
    @Override
    public void tryHighlightNextChapter(ReadingProgress progress) {

        Optional<BookChapter> nextChapter = chapterRepo.findNextChapter(
                progress.getBook().getBookId(),
                progress.getCurrentPage()
        );

        if (nextChapter.isEmpty()) return;

        if (!highlightRepo.existsByChapter_ChapterIdAndSource(
                nextChapter.get().getChapterId(), "AI")) {

            eventPublisher.publishEvent(
                    new AutoHighlightChapterEvent(
                            nextChapter.get().getChapterId(),
                            progress.getUser().getUserId()
                    )
            );
        }
    }

    @Transactional
    @Override
    public BookHighlight highlight(User user, HighlightRequest req) {

        Book book = bookRepo.findById(req.getBookId()).orElseThrow();
        BookChapter chapter = chapterRepo.findById(req.getChapterId()).orElseThrow();

        AIHighlightResponse ai = aiClient.highlight(req.getText());

        BookHighlight h = new BookHighlight();
        h.setUser(user);
        h.setBook(book);
        h.setChapter(chapter);
        h.setText(req.getText());
        h.setHighlightLen((long) req.getText().length());
        h.setPageNumber(req.getPageNumber());
        h.setPosition(req.getPosition());
        h.setSource(req.getSource());
        h.setSentiment(ai.getSentiment());
        h.setAiSummary(ai.getSummary());
        h.setKeywords(ai.getKeywords());

        return repo.save(h);
    }

    @Transactional
    @Override
    public void autoHighlightChapter(
            Long chapterId
    ) {
        BookChapter chapter = chapterRepo.findById(chapterId)
                .orElse(null);

        if (chapter == null) return;

        // Double-check tránh race condition
        if (highlightRepo.existsByChapter_ChapterIdAndSource(
                chapter.getChapterId(), "AI")) {
            return;
        }
        var chapterContent = chapter.getTextContent();
        var bookTitle = chapter.getBook().getTitle();
        var author = chapter.getBook().getAuthor().getName();
        var chapterTitle = chapter.getChapterTitle();

        String prompt = String.format("""
                        You are a professional literary assistant.

                        Book title: %s
                        Author: %s
                        Chapter: %s

                        Task:
                        - Identify 5–7 important, meaningful, or representative sentences.
                        - These should be key ideas, turning points, or memorable lines.
                        - Do NOT rewrite the content.
                        - Return short highlights only, not a summary.
                        - Neutral tone, no opinions.

                        Chapter content:
                        %s
                        """,
                bookTitle,
                author,
                chapterTitle,
                chapterContent
        );

        List<String> highlights =
                aiClient.generateHighlights(prompt);

        for (String text : highlights) {
            String position = buildTextPosition(chapterContent, text);
            if (position == null) continue;
            AIHighlightResponse ai = aiClient.highlight(text);


            repo.save(BookHighlight.builder()
                    .book(chapter.getBook())
                    .chapter(chapter)
                    .text(text)
                    .highlightLen((long) text.length())
                    .position(position)
                    .sentiment(ai.getSentiment())
                    .aiSummary(ai.getSummary())
                    .keywords(ai.getKeywords())
                    .source("AI")
                    .createdAt(LocalDateTime.now())
                    .build());
        }
    }


    @Override
    public List<BookHighlight> getHighlights(Long bookId, User user) {

        List<BookHighlight> result = new ArrayList<>();

        // 1️⃣ USER highlight
        result.addAll(
                repo.findByBook_BookIdAndUser_UserId(
                        bookId,
                        user.getUserId()
                )
        );

        // 2️⃣ AI highlight (shared)
        result.addAll(
                repo.findByBook_BookIdAndSource(
                        bookId,
                        "AI"
                )
        );

        return result;
    }


    private String buildTextPosition(
            String chapterContent,
            String highlightText
    ) {
        int idx = chapterContent.indexOf(highlightText);
        if (idx < 0) return null;

        int prefixStart = Math.max(0, idx - 40);
        int suffixEnd = Math.min(
                chapterContent.length(),
                idx + highlightText.length() + 40
        );

        String prefix = chapterContent
                .substring(prefixStart, idx)
                .replaceAll("\\s+", " ")
                .trim();

        String suffix = chapterContent
                .substring(idx + highlightText.length(), suffixEnd)
                .replaceAll("\\s+", " ")
                .trim();

        Map<String, Object> pos = Map.of(
                "type", "TEXT_RANGE",
                "exact", highlightText,
                "prefix", prefix,
                "suffix", suffix
        );

        try {
            return new ObjectMapper().writeValueAsString(pos);
        } catch (Exception e) {
            return null;
        }
    }

}
