package com.bookhup.service.impl;

import com.bookhup.dto.request.ai.AISummaryRequest;
import com.bookhup.dto.response.ai.summary.AISummaryAIResult;
import com.bookhup.dto.response.ai.summary.AISummaryResponse;
import com.bookhup.model.Book;
import com.bookhup.model.BookChapter;
import com.bookhup.model.BookSummaryAI;
import com.bookhup.model.User;
import com.bookhup.repository.BookChapterRepository;
import com.bookhup.repository.BookRepository;
import com.bookhup.repository.BookSummaryAIRepository;
import com.bookhup.service.AIClient;
import com.bookhup.service.AISummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AISummaryServiceImpl implements AISummaryService {

    private final BookRepository bookRepo;
    private final BookChapterRepository chapterRepo;
    private final BookSummaryAIRepository summaryRepo;
    private final AIClient aiClient;

    public AISummaryResponse generateSummary(AISummaryRequest req, User user) {

        Book book = bookRepo.findById(req.getBookId())
                .orElseThrow(() -> new RuntimeException("Book not found"));

        BookChapter chapter = null;
        String content;

        if ("CHAPTER".equals(req.getType())) {
            chapter = chapterRepo.findById(req.getChapterId())
                    .orElseThrow(() -> new RuntimeException("Chapter not found"));
            content = chapter.getTextContent();
        } else {
            content = book.getChapters()
                    .stream()
                    .map(BookChapter::getTextContent)
                    .collect(Collectors.joining("\n"));
        }

        // 🔹 Call AI
        AISummaryAIResult aiResult = aiClient.summarize(content, req.getLang());

        BookSummaryAI entity = new BookSummaryAI();
        entity.setBook(book);
        entity.setChapter(chapter);
        entity.setSummaryText(aiResult.getSummary());
        entity.setKeywords(aiResult.getKeywords());
        entity.setTopics(aiResult.getTopics());
        entity.setModelVersion(aiResult.getModelVersion());

        summaryRepo.save(entity);

        return AISummaryResponse.builder()
                .summaryId(entity.getSummaryId())
                .summaryText(entity.getSummaryText())
                .keywords(entity.getKeywords())
                .topics(entity.getTopics())
                .modelVersion(entity.getModelVersion())
                .confidence(aiResult.getConfidence())
                .build();
    }
}

