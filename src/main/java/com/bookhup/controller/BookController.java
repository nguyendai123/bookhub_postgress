package com.bookhup.controller;

import com.bookhup.model.Book;
import com.bookhup.service.BookService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@AllArgsConstructor
@RequestMapping("/api")
public class BookController {
    @Autowired
    private BookService bookService;

    @GetMapping("/books")
    public List<Book> getAllBooks() {
        return bookService.getAllBooks();
    }

    @GetMapping("/books/{bookId}")
    public Optional<Book> getBookById(@PathVariable long bookId) {
        return bookService.getBookById(bookId);
    }

    @GetMapping(value = "/books/findByTitle")
    public ResponseEntity<List<Book>> getBooksByTitle(@RequestParam("title") String title) {
        List<Book> books = bookService.findByTitle(title);
        if (books.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(books);
        }
    }

    @GetMapping("/books/findByAuthor")
    public ResponseEntity<List<Book>> findByAuthor(@RequestParam("author") String author) {
        List<Book> books = bookService.findByAuthor(author);
        if (books.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(books);
        }
    }

    @GetMapping("/books/authors")
    public List<String> getAllAuthors() {
        return bookService.getAllAuthors();
    }

    @PostMapping(value = "/books/add")
    public ResponseEntity<Book> saveBook(@RequestBody  Book book)
            throws IOException {
        Book savedBook = bookService.saveBook(book );
        return ResponseEntity.ok(savedBook);
    }

    @PutMapping("/books/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable("id") Long id, @RequestBody Book updatedBook) {
        Optional<Book> existingBookOptional = bookService.getBookById(id);

        if (existingBookOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Book existingBook = existingBookOptional.get();
        existingBook.setTitle(updatedBook.getTitle());
        existingBook.setImage(updatedBook.getImage());
        existingBook.setAuthor(updatedBook.getAuthor());
        existingBook.setIsbn(updatedBook.getIsbn());
        existingBook.setPage(updatedBook.getPage());
        existingBook.setSummary(updatedBook.getSummary());

        Book updatedBookResult = bookService.saveBook(existingBook);
        return ResponseEntity.ok(updatedBookResult);


    }

    @DeleteMapping("/books/{bookId}")
    public void deleteBook(@PathVariable long bookId) throws IOException {
        bookService.deleteBook(bookId);
    }

    @GetMapping("/books/desired")
    public List<Book> getBooksWithDesiredStatus() {
        return bookService.findBooksWithDesiredStatus();
    }

    @GetMapping("/books/reading")
    public List<Book> getBooksWithReadingStatus() {
        return bookService.findBooksWithReadingStatus();
    }

    @GetMapping("/books/readed")
    public List<Book> getBooksWithReadedStatus() {
        return bookService.findBooksWithReadedStatus();
    }

    @GetMapping("/books/status")
    public List<Book> getBooksWithStatus() {
        return bookService.findBooksWithStatus();
    }
}
