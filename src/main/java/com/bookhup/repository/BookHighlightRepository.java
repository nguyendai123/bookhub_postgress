package com.bookhup.repository;

import com.bookhup.model.BookHighlight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookHighlightRepository extends JpaRepository<BookHighlight, Long> {

    List<BookHighlight> findByBook_BookIdAndUser_UserId(Long bookId, Long userId);

    List<BookHighlight> findTop5ByBookBookIdOrderByHighlightIdDesc(Long bookId);
}

