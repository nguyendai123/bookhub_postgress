package com.bookhup.repository;

import com.bookhup.model.BookHighlight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookHighlightRepository extends JpaRepository<BookHighlight, Long> {

    List<BookHighlight> findByChapterChapterId(Long chapterId);
    List<BookHighlight> findTop5ByBookBookIdOrderByHighlightIdDesc(Long bookId);
}

