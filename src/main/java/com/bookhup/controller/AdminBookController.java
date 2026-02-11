package com.bookhup.controller;

import com.bookhup.controller.dto.BulkBookCreateResponse;
import com.bookhup.dto.request.book.BookCreateRequest;
import com.bookhup.model.Book;
import com.bookhup.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/books")
@RequiredArgsConstructor
public class AdminBookController {

    private final BookService bookService;

    @PreAuthorize("hasAuthority('ADMIN_BOOK_CREATE')")
    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody BookCreateRequest request) {
        return ResponseEntity.ok(bookService.createBook(request));
    }
    @PreAuthorize("hasAuthority('ADMIN_BOOK_CREATE')")
    @PostMapping("/bulk")
    public ResponseEntity<BulkBookCreateResponse> createBooks(@RequestBody List<BookCreateRequest> requests) {
        return ResponseEntity.ok(bookService.createBooksPartial(requests));
    }

}

