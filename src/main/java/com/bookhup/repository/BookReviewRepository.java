package com.bookhup.repository;

import com.bookhup.model.BookReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface BookReviewRepository extends JpaRepository<BookReview, Long> {
    List<BookReview> findByBook_BookIdOrderByCreatedAtDesc(Long bookId);
}

