package com.bookhup.service;

import com.bookhup.dto.request.book.BookCreateRequest;
import com.bookhup.dto.response.book.BookDetailDTO;
import com.bookhup.dto.response.book.BookShelfDTO;
import com.bookhup.model.Book;
import com.bookhup.model.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BookService {
    Page<BookShelfDTO> search(String keyword, User user, int page, int size);

    BookDetailDTO getDetail(Long id);

    Book getBook(Long id);

    Book createBook(BookCreateRequest request);
}

