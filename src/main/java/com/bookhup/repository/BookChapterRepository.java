package com.bookhup.repository;

import com.bookhup.model.BookChapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookChapterRepository extends JpaRepository<BookChapter, Long> {
    /**
     * 🔹 Tìm chương hiện tại theo trang đang đọc
     */
    @Query("""
        SELECT c
        FROM BookChapter c
        WHERE c.book.bookId = :bookId
          AND :currentPage BETWEEN c.startPage AND c.endPage
        ORDER BY c.chapterOrder ASC
        """)
    Optional<BookChapter> findCurrentChapter(
            @Param("bookId") Long bookId,
            @Param("currentPage") Integer currentPage
    );

    /**
     * 🔹 Tìm chương kế tiếp theo trang đang đọc
     */
    @Query("""
        SELECT c
        FROM BookChapter c
        WHERE c.book.bookId = :bookId
          AND c.startPage > :currentPage
        ORDER BY c.startPage ASC
        """)
    Optional<BookChapter> findNextChapter(
            @Param("bookId") Long bookId,
            @Param("currentPage") Integer currentPage
    );
}
