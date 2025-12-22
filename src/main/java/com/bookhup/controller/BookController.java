package com.bookhup.controller;

import com.bookhup.dto.response.ai.bookTrending.BookTrendingDTO;
import com.bookhup.dto.response.book.BookDetailDTO;
import com.bookhup.dto.response.book.BookPdfResponse;
import com.bookhup.dto.response.book.BookShelfDTO;
import com.bookhup.model.Book;
import com.bookhup.model.User;
import com.bookhup.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
    public ResponseEntity<Page<BookShelfDTO>> searchBooks(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestAttribute("currentUser") User user
    ) {
        return ResponseEntity.ok(bookService.search(keyword, user, page, size));
    }


    @PreAuthorize("hasAuthority('BOOK_VIEW')")
    @GetMapping("/{bookId}")
    public ResponseEntity<BookDetailDTO> getBookDetail(@PathVariable Long bookId) {
        return ResponseEntity.ok(bookService.getDetail(bookId));
    }

    @GetMapping("/{bookId}/pdf")
    public ResponseEntity<BookPdfResponse> getBookPdf(
            @PathVariable Long bookId,
            @RequestAttribute("currentUser") User user
    ) {
        return ResponseEntity.ok(bookService.getBookPdf(user, bookId));
    }


    /**
     * UC21 – Lấy danh sách sách đang hot (community trend)
     */
    @GetMapping("/trending")
    public ResponseEntity<List<BookTrendingDTO>> getTrendingBooks(
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(
                bookService.getTrendingBooks(limit)
        );
    }
}
