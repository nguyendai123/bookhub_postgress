package com.bookhup.repository;

import com.bookhup.model.BookSummaryAI;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookSummaryAIRepository extends JpaRepository<BookSummaryAI, Long> {
}