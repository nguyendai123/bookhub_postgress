package com.bookhup.repository;

import com.bookhup.model.BookChapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookChapterRepository extends JpaRepository<BookChapter, Long> {
}
