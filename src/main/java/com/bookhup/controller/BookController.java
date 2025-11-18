//package com.bookhup.controller;
//
//import com.bookhup.model.Book;
//import com.bookhup.service.impl.BookService;
//import lombok.AllArgsConstructor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.Optional;
//
//@RestController
//@CrossOrigin(origins = "*")
//@AllArgsConstructor
//@RequestMapping("/api/books")
//public class BookController {
//    @Autowired
//    private BookService bookService;
//
//    @GetMapping("")
//    public List<Book> getAllBooks() {
//        return bookService.getAllBooks();
//    }
//
//    @GetMapping("/{bookId}")
//    public Optional<Book> getBookById(@PathVariable long bookId) {
//        return bookService.getBookById(bookId);
//    }
//
//    // API search book
//    @GetMapping("/search")
//    public List<Book> searchBooks(
//            @RequestParam(required = false) String title,
//            @RequestParam(required = false) String author) {
//
//        if (title != null && !title.isEmpty()) {
//            return bookService.findByTitle(title);
//        } else if (author != null && !author.isEmpty()) {
//            return bookService.findByAuthor(author);
//        } else {
//            return bookService.getAllBooks();
//        }
//    }
//
//    @GetMapping("/authors")
//    public List<String> getAllAuthors() {
//        return bookService.getAllAuthors();
//    }
//
//    @PostMapping(value = "/books/add")
//    public ResponseEntity<Book> saveBook(@RequestBody  Book book)
//            throws IOException {
//        Book savedBook = bookService.saveBook(book );
//        return ResponseEntity.ok(savedBook);
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<Book> updateBook(@PathVariable("id") Long id, @RequestBody Book updatedBook) {
//        Optional<Book> existingBookOptional = bookService.getBookById(id);
//
//        if (existingBookOptional.isEmpty()) {
//            return ResponseEntity.notFound().build();
//        }
//
//        Book existingBook = existingBookOptional.get();
//        existingBook.setTitle(updatedBook.getTitle());
//        existingBook.setImage(updatedBook.getImage());
//        existingBook.setAuthor(updatedBook.getAuthor());
//        existingBook.setIsbn(updatedBook.getIsbn());
//        existingBook.setPage(updatedBook.getPage());
//        existingBook.setSummary(updatedBook.getSummary());
//
//        Book updatedBookResult = bookService.saveBook(existingBook);
//        return ResponseEntity.ok(updatedBookResult);
//
//
//    }
//
//    @DeleteMapping("/{bookId}")
//    public void deleteBook(@PathVariable long bookId) throws IOException {
//        bookService.deleteBook(bookId);
//    }
//
//    @GetMapping("/desired")
//    public List<Book> getBooksWithDesiredStatus() {
//        return bookService.findBooksWithDesiredStatus();
//    }
//
//    @GetMapping("/reading")
//    public List<Book> getBooksWithReadingStatus() {
//        return bookService.findBooksWithReadingStatus();
//    }
//
//    @GetMapping("/readed")
//    public List<Book> getBooksWithReadedStatus() {
//        return bookService.findBooksWithReadedStatus();
//    }
//
//    @GetMapping("/status")
//    public List<Book> getBooksWithStatus() {
//        return bookService.findBooksWithStatus();
//    }
//}
