package com.bookhup.service;

import com.bookhup.dto.request.book.BookCreateRequest;
import com.bookhup.model.Author;
import com.bookhup.model.Book;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface BookService {
    List<Book> search(String keyword);

    Book getDetail(Long id);

    Book getBook(Long id);

    Book createBook(BookCreateRequest request);
}

