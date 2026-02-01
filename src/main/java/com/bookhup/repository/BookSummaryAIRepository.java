package com.bookhup.repository;

import com.bookhup.model.BookSummaryAI;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookSummaryAIRepository extends JpaRepository<BookSummaryAI, Long> {
    List<BookSummaryAI> findTop3ByBookBookIdOrderBySummaryIdDesc(Long bookId);

    Optional<BookSummaryAI> findByBook_BookIdAndChapter_ChapterIdAndLang(Long bookId, Long chapterId, String lang);
}