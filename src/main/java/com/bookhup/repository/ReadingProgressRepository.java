package com.bookhup.repository;

import com.bookhup.model.Book;
import com.bookhup.model.ReadingProgress;
import com.bookhup.model.ReadingStatus;
import com.bookhup.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    /*Goi y sach theo lich su doc*/

    @Query("""
                SELECT rp.book.bookId
                FROM ReadingProgress rp
                WHERE rp.user.userId = :userId
                  AND rp.readingStatus IN (:statuses)
                GROUP BY rp.book.bookId
                ORDER BY MAX(rp.lastUpdated) DESC
            """)
    List<Long> findHistoryBookIds(
            @Param("userId") Long userId,
            @Param("statuses") List<ReadingStatus> statuses
    );

    @Query(value = """
            SELECT g.name
            FROM reading_progress rp
            JOIN book_genre bg ON rp.book_id = bg.book_id
            JOIN genres g ON bg.genre_id = g.genre_id
            WHERE rp.user_id = :userId
            GROUP BY g.genre_id, g.name
            ORDER BY COUNT(*) DESC
            LIMIT 5
            """, nativeQuery = true)
    List<String> findTopGenres(@Param("userId") Long userId);

    @Query("""
                SELECT rp.book.bookId, rp.percentDone
                FROM ReadingProgress rp
                WHERE rp.user.userId = :userId
            """)
    List<Object[]> findCompletionRates(@Param("userId") Long userId);

    @Query("""
                SELECT rp.book.bookId, rp.lastUpdated
                FROM ReadingProgress rp
                WHERE rp.user.userId = :userId
            """)
    List<Object[]> findReadingRecency(@Param("userId") Long userId);

    @Query("""
                SELECT AVG(b.totalPages)
                FROM ReadingProgress rp
                JOIN rp.book b
                WHERE rp.user.userId = :userId
                  AND rp.percentDone >= 60
            """)
    Optional<Float> findAvgBookLength(@Param("userId") Long userId);


    /*Goi y sach theo lich su doc*/
}

