package com.bookhup.repository;

import com.bookhup.model.BookHighlight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookHighlightRepository extends JpaRepository<BookHighlight, Long> {

    List<BookHighlight> findByBook_BookIdAndUser_UserId(Long bookId, Long userId);

    List<BookHighlight> findTop5ByBookBookIdOrderByHighlightIdDesc(Long bookId);

    @Query("""
                SELECT bh.book.bookId, COUNT(bh)
                FROM BookHighlight bh
                WHERE bh.user.userId = :userId
                GROUP BY bh.book.bookId
            """)
    List<Object[]> findHighlightCounts(@Param("userId") Long userId);

    boolean existsByChapter_ChapterId(Long chapterId);
}

