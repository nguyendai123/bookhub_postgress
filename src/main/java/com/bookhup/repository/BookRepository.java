package com.bookhup.repository;

import com.bookhup.model.Author;
import com.bookhup.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByTitle(String title);

    List<Book> findByAuthor(Author author);

    @Query("SELECT DISTINCT b.author FROM Book b")
    List<String> findAllAuthors();

    @Query("""
        SELECT b FROM Book b
        WHERE (:keyword IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(b.isbn) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(b.genres) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(b.author.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
    """)
    List<Book> searchBooks(String keyword);

}
