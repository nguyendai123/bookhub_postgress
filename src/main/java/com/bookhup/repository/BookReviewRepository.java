package com.bookhup.repository;

import com.bookhup.model.BookReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookReviewRepository extends JpaRepository<BookReview, Long> {
    List<BookReview> findByBook_BookIdOrderByCreatedAtDesc(Long bookId);

    @Query("""
        select br.ownerId
        from BookReview br
        where br.reviewId = :reviewId
    """)
    Optional<Long> findOwnerId(@Param("reviewId") Long reviewId);
}

