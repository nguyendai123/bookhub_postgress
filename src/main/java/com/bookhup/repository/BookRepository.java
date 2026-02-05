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

    @Query("""
                select distinct b
                from Book b
                join b.genres g
                where g.name in :genres
                  and b.bookId not in :excludedIds
                order by b.avgRating desc
            """)
    List<Book> findBooksByGenres(
            @Param("genres") List<String> genres,
            @Param("excludedIds") List<Long> excludedIds
    );

    @Query("""
                select b
                from Book b
                where b.author.authorId in (
                    select distinct rp.book.author.authorId
                    from ReadingProgress rp
                    where rp.user.userId = :userId
                )
                and b.bookId not in :excludedIds
            """)
    List<Book> findBooksByFavoriteAuthors(
            @Param("userId") Long userId,
            @Param("excludedIds") List<Long> excludedIds
    );

    @Query(value = """
            SELECT DISTINCT b.*
            FROM books b
            WHERE b.language = (
                SELECT b2.language
                FROM reading_progress rp
                JOIN books b2 ON rp.book_id = b2.book_id
                WHERE rp.user_id = :userId
                GROUP BY b2.language
                ORDER BY COUNT(*) DESC
                LIMIT 1
            )
            AND b.book_id <> ALL(:historyBookIds)
            """, nativeQuery = true)
    List<Book> findBooksSameLanguage(
            @Param("userId") Long userId,
            @Param("historyBookIds") List<Long> historyBookIds
    );

}
