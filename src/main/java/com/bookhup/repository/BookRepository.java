package com.bookhup.repository;

import com.bookhup.model.Author;
import com.bookhup.model.Book;
import com.bookhup.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByTitle(String title);

    List<Book> findByAuthor(Author author);

    @Query("SELECT DISTINCT b.author FROM Book b")
    List<String> findAllAuthors();

    @Query("""
                SELECT DISTINCT b
                FROM Book b
                JOIN FETCH b.author a
                LEFT JOIN FETCH b.genres g
                LEFT JOIN ReadingProgress rp 
                    ON rp.book = b AND rp.user = :user
                WHERE (:keyword IS NULL
                    OR LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(b.isbn) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(a.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(g.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                )
            """)
    Page<Book> searchBooksWithUserStatus(String keyword, User user, Pageable pageable);


    // Tìm sách theo ISBN
    Optional<Book> findByIsbn(String isbn);

    // Kiểm tra trùng sách theo (title + author_id)
    boolean existsByTitleIgnoreCaseAndAuthor_AuthorId(String title, Long authorId);


    @EntityGraph(attributePaths = {
            "author",
            "genres",
            "bookReviews",
            "chapters",
            "mediaAssets",
            "highlights",
            "quotes",
            "readingProgresses"
    })
    Optional<Book> findDetailByBookId(Long bookId);

    @Query(value = """
            SELECT g.name
            FROM reading_progress rp
            JOIN book_genre bg ON rp.book_id = bg.book_id
            JOIN genres g ON bg.genre_id = g.genre_id
            WHERE rp.user_id = :userId
            GROUP BY g.name
            ORDER BY COUNT(*) DESC
            """, nativeQuery = true)
    List<String> findTopGenres(@Param("userId") Long userId);

}
