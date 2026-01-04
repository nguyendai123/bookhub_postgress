package com.bookhup.service.impl;

import com.bookhup.dto.response.ai.recommendation.RecommendationDTO;
import com.bookhup.dto.response.book.GenreDTO;
import com.bookhup.model.Book;
import com.bookhup.model.RecommendationLog;
import com.bookhup.model.User;
import com.bookhup.repository.BookHighlightRepository;
import com.bookhup.repository.BookRepository;
import com.bookhup.repository.ReadingProgressRepository;
import com.bookhup.repository.RecommendationLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static com.bookhup.model.ReadingStatus.FINISHED;
import static com.bookhup.model.ReadingStatus.READING;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    @Value("${bookhub.recommendation.score.genre}")
    private float genreScore;

    @Value("${bookhub.recommendation.score.author}")
    private float authorScore;

    @Value("${bookhub.recommendation.score.history}")
    private float historyScore;

    @Value("${bookhub.recommendation.score.highlight}")
    private float highlight;

    @Value("${bookhub.recommendation.score.same-language}")
    private float sameLanguageScore;

    private final BookRepository bookRepo;
    private final ReadingProgressRepository readingRepo;
    private final BookHighlightRepository highlightRepo;
    private final RecommendationLogRepository recommendationLogRepository;

    private final int limitPerAuthor = 2;
    private final int finalLimit = 10;
    private final int shuffleTopN = 10;


    public List<RecommendationDTO> recommend(User user) {
        // 1. Lịch sử đọc
        var historyBookIds = readingRepo.findHistoryBookIds(user.getUserId(), List.of(READING, FINISHED));
        if (historyBookIds.isEmpty()) {
            return List.of(); // user mới
        }

        // 2. Genre yêu thích
        List<String> topGenres = readingRepo.findTopGenres(user.getUserId());

        Map<Long, Float> scoreMap = new HashMap<>();

        // 3. Gợi ý theo GENRE
        List<Book> genreBooks = bookRepo.findBooksByGenres(topGenres, historyBookIds);
        genreBooks.forEach(b ->
                scoreMap.merge(b.getBookId(), genreScore, Float::sum)
        );

        // 4. Gợi ý theo AUTHOR
        List<Book> authorBooks = bookRepo.findBooksByFavoriteAuthors(user.getUserId(), historyBookIds);
        authorBooks.forEach(b ->
                scoreMap.merge(b.getBookId(), authorScore, Float::sum)
        );

        // 5. Ưu tiên sách cùng ngôn ngữ
        bookRepo.findBooksSameLanguage(user.getUserId(), historyBookIds)
                .forEach(book ->
                        scoreMap.merge(
                                book.getBookId(),
                                sameLanguageScore,
                                Float::sum
                        )
                );
        // 6. Diem hoan thanh
        readingRepo.findCompletionRates(user.getUserId()).forEach(row -> {
            Long bookId = (Long) row[0];
            Float percent = (Float) row[1];

            if (percent >= 80) {
                scoreMap.merge(bookId, historyScore, Float::sum);
            } else if (percent < 20) {
                scoreMap.merge(bookId, -historyScore, Float::sum); // phạt
            }
        });

        // 6.độ dài / độ khó – chống “recommend sai trình”
        Float avgPages = readingRepo.findAvgBookLength(user.getUserId());

        readingRepo.findReadingRecency(user.getUserId()).forEach(row -> {
            Long bookId = (Long) row[0];
            LocalDateTime lastRead = (LocalDateTime) row[1];

            long days = ChronoUnit.DAYS.between(lastRead, LocalDateTime.now());

            float decay =
                    days <= 30 ? 1.0f :
                            days <= 90 ? 0.7f :
                                    days <= 180 ? 0.4f : 0.1f;

            scoreMap.merge(bookId, decay * historyScore, Float::sum);
        });

        //7. interaction – quan trọng hơn
        highlightRepo.findHighlightCounts(user.getUserId()).forEach(row -> {
            Long bookId = (Long) row[0];
            Long count = (Long) row[1];

            scoreMap.merge(bookId, count * highlight, Float::sum);
        });

        Map<Long, Book> bookMap =
                bookRepo.findAllById(scoreMap.keySet())
                        .stream()
                        .collect(Collectors.toMap(Book::getBookId, b -> b));

        // 8. Cộng điểm theo rating
        Map<Long, Integer> authorCount = new HashMap<>();

        List<RecommendationDTO> candidates =
                scoreMap.entrySet()
                        .stream()
                        // 1️⃣ lọc theo độ dài
                        .filter(entry -> {
                            Book book = bookMap.get(entry.getKey());
                            if (book == null || book.getTotalPages() == null) return false;

                            return Math.abs(book.getTotalPages() - avgPages)
                                   <= avgPages * 0.7;
                        })
                        // 2️⃣ map + cộng điểm
                        .map(entry -> {
                            Book book = bookMap.get(entry.getKey());

                            float score = entry.getValue();
                            if (book.getAvgRating() != null) {
                                score += book.getAvgRating();
                            }

                            return new AbstractMap.SimpleEntry<>(book, score);
                        })
                        // 3️⃣ sort theo score
                        .sorted((a, b) -> Float.compare(b.getValue(), a.getValue()))
                        // 4️⃣ giới hạn số sách mỗi author
                        .filter(entry -> {
                            Long authorId = entry.getKey().getAuthor().getAuthorId();
                            int count = authorCount.getOrDefault(authorId, 0);

                            if (count >= limitPerAuthor) return false;

                            authorCount.put(authorId, count + 1);
                            return true;
                        })
                        // 5️⃣ convert DTO + log
                        .map(entry -> {
                            Book book = entry.getKey();
                            float score = entry.getValue();

                            recommendationLogRepository.save(
                                    RecommendationLog.builder()
                                            .user(user)
                                            .recommendedBookId(book.getBookId())
                                            .confidenceScore(score)
                                            .feedback(null)
                                            .timestamp(LocalDateTime.now())
                                            .ownerId(user.getUserId())
                                            .build()
                            );

                            return RecommendationDTO.builder()
                                    .bookId(book.getBookId())
                                    .title(book.getTitle())
                                    .authorName(book.getAuthor().getName())
                                    .genres(
                                            book.getGenres().stream()
                                                    .map(g -> new GenreDTO(g.getGenreId(), g.getName()))
                                                    .collect(Collectors.toSet())
                                    )
                                    .avgRating(book.getAvgRating())
                                    .coverUrl(book.getCoverUrl())
                                    .confidenceScore(score)
                                    .reason("Gợi ý theo lịch sử đọc")
                                    .build();
                        })
                        .collect(Collectors.toList());

        int shuffleLimit = Math.min(shuffleTopN, candidates.size());

        List<RecommendationDTO> topPart =
                new ArrayList<>(candidates.subList(0, shuffleLimit));

        Collections.shuffle(topPart);

        List<RecommendationDTO> result = new ArrayList<>();
        result.addAll(topPart);

        if (candidates.size() > shuffleLimit) {
            result.addAll(candidates.subList(shuffleLimit, candidates.size()));
        }

        return result.stream()
                .limit(finalLimit)
                .toList();
    }
}

