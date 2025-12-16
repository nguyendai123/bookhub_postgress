package com.bookhup.repository;

import com.bookhup.model.Book;
import com.bookhup.model.ReadingProgress;
import com.bookhup.model.ReadingStatus;
import com.bookhup.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReadingProgressRepository extends JpaRepository<ReadingProgress, Long> {
    List<ReadingProgress> findByUser(User user);

    List<ReadingProgress> findByUserAndReadingStatus(
            User user,
            ReadingStatus status
    );

    Optional<ReadingProgress> findByUserAndBook(
            User user,
            Book book
    );

    Optional<ReadingProgress> findByUser_UserIdAndBook_BookId(Long userId, Long bookId);
}

