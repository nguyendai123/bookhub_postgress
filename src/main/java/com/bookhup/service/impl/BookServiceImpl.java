package com.bookhup.service.impl;

import com.bookhup.model.Book;
import com.bookhup.repository.BookRepository;
import com.bookhup.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    @Override
    public List<Book> search(String keyword) {
        return bookRepository.searchBooks(keyword);
    }

    @Override
    public Book getDetail(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));
    }

    @Override
    public Book createBook(Book book) {
        book.setCreatedAt(LocalDateTime.now());
        return bookRepository.save(book);
    }
}

