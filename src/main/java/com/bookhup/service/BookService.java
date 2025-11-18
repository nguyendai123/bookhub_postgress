package com.bookhup.service;

import com.bookhup.model.Book;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface BookService {

    List<Book> getAllBooks();

    Optional<Book> getBookById(long bookId);

    List<Book> findByTitle(String title);

    Book saveBook(Book book);

    Book saveBookwithMultiFile(Book book, MultipartFile imageFile) throws IOException;

    void deleteBook(long bookId) throws IOException;

    List<Book> findByAuthor(String author);

    List<String> getAllAuthors();

    List<Book> findBooksWithDesiredStatus();

    List<Book> findBooksWithReadingStatus();

    List<Book> findBooksWithReadedStatus();

    List<Book> findBooksWithStatus();

    Book findById(Long bookId);
}

