package com.bookhup.service.impl;

import com.bookhup.repository.BookRepository;
import com.bookhup.Upload.FileUploadUtil;
import com.bookhup.model.Book;
import com.bookhup.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    @Override
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Override
    public Optional<Book> getBookById(long bookId) {
        return bookRepository.findById(bookId);
    }

    @Override
    public List<Book> findByTitle(String title) {
        return bookRepository.findByTitle(title);
    }

    @Override
    public Book saveBookwithMultiFile(Book book, MultipartFile imageFile) throws IOException {
        String fileName = StringUtils.cleanPath(imageFile.getOriginalFilename());
        book.setImage(fileName);

        Book savedBook = bookRepository.save(book);

        String uploadDir = "image/" + savedBook.getBookID();
        FileUploadUtil.saveFile(uploadDir, fileName, imageFile);

        return savedBook;
    }

    @Override
    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public void deleteBook(long bookId) throws IOException {
        String uploadDir = "image\\" + bookId;
        FileUploadUtil.deleteFile(uploadDir);
        bookRepository.deleteById(bookId);
    }

    @Override
    public List<Book> findByAuthor(String author) {
        return bookRepository.findByAuthor(author);
    }

    @Override
    public List<String> getAllAuthors() {
        return bookRepository.findAllAuthors();
    }

    @Override
    public List<Book> findBooksWithDesiredStatus() {
        return bookRepository.findBooksWithDesiredStatus();
    }

    @Override
    public List<Book> findBooksWithReadingStatus() {
        return bookRepository.findBooksWithReadingStatus();
    }

    @Override
    public List<Book> findBooksWithReadedStatus() {
        return bookRepository.findBooksWithReadedStatus();
    }

    @Override
    public List<Book> findBooksWithStatus() {
        return bookRepository.findBooksWithStatus();
    }

    @Override
    public Book findById(Long bookId) {
        return bookRepository.findById(bookId).orElse(null);
    }
}

