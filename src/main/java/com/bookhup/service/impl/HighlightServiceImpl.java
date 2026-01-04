package com.bookhup.service.impl;

import com.bookhup.dto.request.ai.highLight.HighlightRequest;
import com.bookhup.dto.response.ai.highLight.AIHighlightResponse;
import com.bookhup.model.Book;
import com.bookhup.model.BookChapter;
import com.bookhup.model.BookHighlight;
import com.bookhup.model.User;
import com.bookhup.repository.BookChapterRepository;
import com.bookhup.repository.BookHighlightRepository;
import com.bookhup.repository.BookRepository;
import com.bookhup.service.AIClient;
import com.bookhup.service.HighlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HighlightServiceImpl implements HighlightService {

    private final AIClient aiClient;
    private final BookHighlightRepository repo;
    private final BookRepository bookRepo;
    private final BookChapterRepository chapterRepo;

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
    public void autoHighlightChapter(User user, BookChapter chapter) {

        // ⛔ Tránh chạy lại
        if (repo.existsByChapter_ChapterId(chapter.getChapterId())) return;

        List<String> highlights =
                aiClient.generateHighlights(chapter.getTextContent());

        for (String text : highlights) {
            AIHighlightResponse ai = aiClient.highlight(text);

            repo.save(BookHighlight.builder()
                    .user(user)
                    .book(chapter.getBook())
                    .chapter(chapter)
                    .text(text)
                    .sentiment(ai.getSentiment())
                    .aiSummary(ai.getSummary())
                    .keywords(ai.getKeywords())
                    .source("AI")
                    .createdAt(LocalDateTime.now())
                    .ownerId(user.getUserId())
                    .build());
        }
    }


    @Override
    public List<BookHighlight> getHighlights(Long chapterId, User user) {
        return repo.findByBook_BookIdAndUser_UserId(chapterId, user.getUserId());
    }
}
