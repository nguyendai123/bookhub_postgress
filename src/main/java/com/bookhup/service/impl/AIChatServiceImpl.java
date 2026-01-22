package com.bookhup.service.impl;

import com.bookhup.dto.request.ai.aiInteraction.AIAskRequest;
import com.bookhup.dto.request.ai.aiInteraction.AIContext;
import com.bookhup.dto.response.ai.aiInteraction.AIAnswerResponse;
import com.bookhup.dto.response.ai.aiInteraction.AIChatHistoryDTO;
import com.bookhup.model.*;
import com.bookhup.repository.AIInteractionRepository;
import com.bookhup.repository.BookHighlightRepository;
import com.bookhup.repository.BookRepository;
import com.bookhup.repository.BookSummaryAIRepository;
import com.bookhup.service.AIChatService;
import com.bookhup.service.AIClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AIChatServiceImpl implements AIChatService {

    private final AIClient aiClient;
    private final AIInteractionRepository repo;

    private final BookRepository bookRepo;
    private final BookSummaryAIRepository summaryRepo;
    private final BookHighlightRepository highlightRepo;

    @Override
    public List<AIChatHistoryDTO> getChatHistory(Long userId, Long bookId) {

        return repo
                .findByUser_UserIdAndBook_BookIdOrderByCreatedAtAsc(userId, bookId)
                .stream()
                .map(i -> AIChatHistoryDTO.builder()
                        .interactionId(i.getInteractionId())
                        .question(i.getQuestion())
                        .answer(i.getAnswer())
                        .createdAt(i.getCreatedAt())
                        .build())
                .toList();
    }

    @Transactional
    @Override
    public AIInteraction ask(User user, AIAskRequest req) {
        // 1️⃣ Load book
        Book book = bookRepo.findById(req.getBookId())
                .orElseThrow(() -> new RuntimeException("Book not found"));

        var bookId = book.getBookId();

        // 2️⃣ Load AI summaries (context ngắn gọn)
        List<String> summaries = summaryRepo
                .findTop3ByBookBookIdOrderBySummaryIdDesc(bookId)
                .stream()
                .map(BookSummaryAI::getSummaryText)
                .toList();

        // 3️⃣ Load user highlights (nếu có)
        List<String> highlights = highlightRepo
                .findTop5ByBookBookIdOrderByHighlightIdDesc(bookId)
                .stream()
                .map(BookHighlight::getText)
                .toList();

        // 4️⃣ Build context
        AIContext context = new AIContext(
                book.getTitle(),
                summaries,
                highlights
        );

        // 5️⃣ Build request
        req = new AIAskRequest(
                bookId,
                req.getQuestion(),
                context
        );

        AIAnswerResponse ai = aiClient.ask(req, req.getBookId());

        AIInteraction entity = new AIInteraction();
        entity.setUser(user);
        entity.setBook(book);
        entity.setQuestion(req.getQuestion());
        entity.setAnswer(ai.getAnswer());
        entity.setConfidenceScore(ai.getConfidence());
        entity.setModelVersion("gpt-4o");
        entity.setCreatedAt(LocalDateTime.now());

        return repo.save(entity);
    }
}
