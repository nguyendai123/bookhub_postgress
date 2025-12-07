package com.bookhup.controller;

import com.bookhup.model.Book;
import com.bookhup.model.User;
import com.bookhup.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @PreAuthorize("hasAuthority('BOOK_SEARCH')")
    @GetMapping("/search")
    public ResponseEntity<List<Book>> searchBooks(@RequestAttribute("currentUser") User user, @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(bookService.search(keyword));
    }

    @PreAuthorize("hasAuthority('BOOK_VIEW')")
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookDetail(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getDetail(id));
    }
}
