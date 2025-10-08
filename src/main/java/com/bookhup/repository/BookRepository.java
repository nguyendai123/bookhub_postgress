package com.bookhup.repository;

import com.bookhup.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByTitle(String title);

    List<Book> findByAuthor(String author);

    List<Book> findBooksByGenres_GenreID(Long genreID);

    @Query("SELECT DISTINCT b.author FROM Book b")
    List<String> findAllAuthors();

    @Query("SELECT DISTINCT b FROM Book b JOIN b.progresses p WHERE p.status = 'Muốn đọc'")
    List<Book> findBooksWithDesiredStatus();

    @Query("SELECT DISTINCT b FROM Book b JOIN b.progresses p WHERE p.status = 'Đang đọc'")
    List<Book> findBooksWithReadingStatus();

    @Query("SELECT DISTINCT b FROM Book b JOIN b.progresses p WHERE p.status = 'Đã đọc'")
    List<Book> findBooksWithReadedStatus();

    @Query("SELECT p.status FROM Book b JOIN b.progresses p")
    List<Book> findBooksWithStatus();

}
