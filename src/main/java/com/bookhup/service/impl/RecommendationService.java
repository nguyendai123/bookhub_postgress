package com.bookhup.service.impl;

import com.bookhup.dto.request.ai.recommendation.AIRecommendationRequest;
import com.bookhup.dto.response.ai.recommendation.AIRecommendationResponse;
import com.bookhup.dto.response.ai.recommendation.RecommendationDTO;
import com.bookhup.model.Book;
import com.bookhup.model.RecommendationLog;
import com.bookhup.model.User;
import com.bookhup.repository.BookRepository;
import com.bookhup.repository.ReadingProgressRepository;
import com.bookhup.repository.RecommendationLogRepository;
import com.bookhup.service.AIClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final AIClient aiClient;
    private final BookRepository bookRepo;
    private final ReadingProgressRepository readingProgressRepository;
    private final RecommendationLogRepository logRepo;

    public List<RecommendationDTO> recommend(User user) {

        AIRecommendationRequest req = new AIRecommendationRequest();
        req.setUserId(user.getUserId());
        req.setHistoryBookIds(readingProgressRepository.findHistoryBookIds(user.getUserId()));
        req.setGenres(bookRepo.findTopGenres(user.getUserId()));

        AIRecommendationResponse res = aiClient.recommend(req);

        return res.getRecommendations().stream().map(item -> {
            Book book = bookRepo.findById(item.getBookId()).orElseThrow();

            logRepo.save(
                    RecommendationLog.builder()
                            .user(user)
                            .recommendedBookId(item.getBookId())
                            .algorithm(item.getAlgorithm())
                            .confidenceScore(item.getConfidence())
                            .feedback(null) // hoặc "IGNORE"
                            .timestamp(LocalDateTime.now())
                            .ownerId(user.getUserId())
                            .build()
            );


            return new RecommendationDTO(
                    book.getBookId(),
                    book.getTitle(),
                    item.getConfidence(),
                    "Gợi ý theo lịch sử đọc"
            );
        }).toList();
    }
}

