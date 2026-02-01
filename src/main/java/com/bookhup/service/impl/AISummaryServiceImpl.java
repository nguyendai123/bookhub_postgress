package com.bookhup.service.impl;

import com.bookhup.dto.request.ai.summary.AISummaryRequest;
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

import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AISummaryServiceImpl implements AISummaryService {

    private final BookRepository bookRepo;
    private final BookChapterRepository chapterRepo;
    private final BookSummaryAIRepository summaryRepo;
    private final AIClient aiClient;

    @Override
    public AISummaryResponse generateSummary(AISummaryRequest req, User user) {

        Book book = bookRepo.findById(req.getBookId())
                .orElseThrow(() -> new RuntimeException("Book not found"));

        BookChapter chapter = null;
        String content;
        String scope;

        if ("CHAPTER".equalsIgnoreCase(req.getType())) {
            chapter = chapterRepo.findById(req.getChapterId())
                    .orElseThrow(() -> new RuntimeException("Chapter not found"));
            content = chapter.getTextContent();
            scope = "CHAPTER";
        } else {
            content = book.getChapters()
                    .stream()
                    .map(BookChapter::getTextContent)
                    .collect(Collectors.joining("\n"));
            scope = "BOOK";
        }

        Long chapterId = chapter != null ? chapter.getChapterId() : null;
        String lang = req.getLang().toLowerCase();

        // ==============================
        // 🔹 1. CHECK ĐÚNG NGÔN NGỮ TRƯỚC
        // ==============================
        Optional<BookSummaryAI> existing = summaryRepo
                .findByBook_BookIdAndChapter_ChapterIdAndLang(book.getBookId(), chapterId, lang);

        if (existing.isPresent()) {
            return mapToResponse(existing.get(), 1.0f);
        }

        // ==============================
        // 🔹 2. NẾU KHÔNG CÓ → TÌM BẢN TIẾNG ANH
        // ==============================
        Optional<BookSummaryAI> existingEn = summaryRepo
                .findByBook_BookIdAndChapter_ChapterIdAndLang(book.getBookId(), chapterId, "en");

        if (existingEn.isPresent()) {
            BookSummaryAI enSummary = existingEn.get();

            // 👉 Dịch sang ngôn ngữ yêu cầu
            String translated = aiClient.translate(enSummary.getSummaryText(), "en", lang);

            // 👉 Lưu bản dịch vào DB để lần sau khỏi dịch lại
            BookSummaryAI newLangSummary = BookSummaryAI.builder()
                    .book(book)
                    .chapter(chapter)
                    .lang(lang)
                    .summaryText(translated)
                    .keywords(enSummary.getKeywords())
                    .topics(enSummary.getTopics())
                    .modelVersion(enSummary.getModelVersion())
                    .ownerId(user.getUserId())
                    .build();

            summaryRepo.save(newLangSummary);

            return mapToResponse(newLangSummary, 0.95f);
        }

        // ==============================
        // 🔹 3. KHÔNG CÓ EN → GỌI AI GENERATE
        // ==============================
        AISummaryAIResult aiResult = aiClient.summarize(
                content,
                book.getTitle(),
                book.getLanguage(),
                book.getAuthor().getName(),
                scope,
                "en"   // LUÔN generate EN làm gốc
        );

        String englishSummary = aiResult.getSummary();

        if (englishSummary == null) {
            throw new RuntimeException("AI did not return English summary");
        }

        // 👉 Lưu bản EN
        BookSummaryAI savedEn = BookSummaryAI.builder()
                .book(book)
                .chapter(chapter)
                .lang("en")
                .summaryText(englishSummary)
                .keywords(aiResult.getKeywords())
                .topics(aiResult.getTopics())
                .modelVersion(aiResult.getModelVersion())
                .ownerId(user.getUserId())
                .build();

        summaryRepo.save(savedEn);

        // 👉 Nếu user yêu cầu EN thì trả luôn
        if ("en".equals(lang)) {
            return mapToResponse(savedEn, aiResult.getConfidence());
        }

        // 👉 Nếu yêu cầu ngôn ngữ khác → dịch từ EN
        String translated = aiClient.translate(englishSummary, "en", lang);

        BookSummaryAI savedTranslated = BookSummaryAI.builder()
                .book(book)
                .chapter(chapter)
                .lang(lang)
                .summaryText(translated)
                .keywords(aiResult.getKeywords())
                .topics(aiResult.getTopics())
                .modelVersion(aiResult.getModelVersion())
                .ownerId(user.getUserId())
                .build();

        summaryRepo.save(savedTranslated);

        return mapToResponse(savedTranslated, aiResult.getConfidence());
    }

    private AISummaryResponse mapToResponse(BookSummaryAI s, float confidence) {
        return AISummaryResponse.builder()
                .summaryId(s.getSummaryId())
                .summaryText(s.getSummaryText())
                .keywords(s.getKeywords())
                .topics(s.getTopics())
                .modelVersion(s.getModelVersion())
                .confidence(confidence)
                .lang(s.getLang())
                .build();
    }


    @Override
    public AISummaryResponse getSummary(Long bookId, Long chapterId, String lang) {

        BookSummaryAI summary = summaryRepo
                .findByBook_BookIdAndChapter_ChapterIdAndLang(bookId, chapterId, lang)
                .orElseGet(() ->
                        summaryRepo.findByBook_BookIdAndChapter_ChapterIdAndLang(bookId, chapterId, "en")
                                .orElseThrow(() -> new RuntimeException("Summary not found"))
                );

        return AISummaryResponse.builder()
                .summaryId(summary.getSummaryId())
                .summaryText(summary.getSummaryText())
                .keywords(summary.getKeywords())
                .topics(summary.getTopics())
                .modelVersion(summary.getModelVersion())
                .confidence(1.0f)
                .lang(summary.getLang())
                .build();
    }
}

