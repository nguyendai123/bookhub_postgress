package com.thanhson.bookhup.controller;

import com.thanhson.bookhup.exception.ResourceNotFoundException;
import com.thanhson.bookhup.model.Book;
import com.thanhson.bookhup.model.Genre;
import com.thanhson.bookhup.repository.BookRepository;
import com.thanhson.bookhup.repository.GenreRepository;
import com.thanhson.bookhup.service.GenreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")

@RequestMapping("/api")
public class GenreController {
    @Autowired
    private GenreService genreService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private GenreRepository genreRepository;

    @GetMapping("/genres")
    public ResponseEntity<List<Genre>> getAllGenres() {
        List<Genre> genres = genreService.getAllGenres();
        return ResponseEntity.ok(genres);
    }

    @GetMapping("/genres/{id}")
    public ResponseEntity<Genre> getGenreById(@PathVariable("id") Long id) {
        Optional<Genre> genre = genreService.getGenreById(id);
        return genre.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/books/{bookID}/genres")
    public ResponseEntity<List<Genre>> getGenresByBooksId(@PathVariable(value = "bookID") Long bookID) {
        if (!bookRepository.existsById(bookID)) {
            throw new ResourceNotFoundException("Not found Tutorial with id = " + bookID);
        }

        List<Genre> genres = genreRepository.findGenresByBooks_BookID(bookID);
        return new ResponseEntity<>(genres, HttpStatus.OK);
    }

    @GetMapping("/genres/{genreID}/books")
    public ResponseEntity<List<Book>> getAllBooksByGenreId(@PathVariable(value = "genreID") Long genreID) {
        if (!genreRepository.existsById(genreID)) {
            throw new ResourceNotFoundException("Not found Tag  with id = " + genreID);
        }

        List<Book> books = bookRepository.findBooksByGenres_GenreID(genreID);
        return new ResponseEntity<>(books, HttpStatus.OK);
    }
    @GetMapping(value="/findByName")
    public ResponseEntity<List<Genre>> getGenreByName(@RequestParam("name") String name) {
        List<Genre> genres = genreService.findByName(name);
        if (genres.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(genres);
        }
    }
    @PostMapping("/genres")
    public ResponseEntity<Genre> createGenre(@RequestBody Genre genre) {
        Genre createdGenre = genreService.createGenre(genre);
        return ResponseEntity.ok(createdGenre);
    }
    @PostMapping("/books/{bookID}/genres")
    public ResponseEntity<Genre> addGenre(@PathVariable(value = "bookID") Long bookID, @RequestBody Genre genreRequest) {
        Genre genre = bookRepository.findById(bookID).map(book -> {
            long genreID = genreRequest.getGenreID();

            // tag is existed
            if (genreID != 0L) {
                Genre _genre = genreRepository.findById(genreID)
                        .orElseThrow(() -> new ResourceNotFoundException("Not found Tag with id = " + genreID));
                book.addGenre(_genre);
                bookRepository.save(book);
                return _genre;
            }

            // add and create new Tag
            book.addGenre(genreRequest);
            return genreRepository.save(genreRequest);
        }).orElseThrow(() -> new ResourceNotFoundException("Not found Tutorial with id = " + bookID));

        return new ResponseEntity<>(genre, HttpStatus.CREATED);
    }

    @PutMapping("/genres/{id}")
    public ResponseEntity<Genre> updateGenre(@PathVariable("id") Long id, @RequestBody Genre updatedGenre) {
        Optional<Genre> existingGenre = genreService.getGenreById(id);
        if (existingGenre.isPresent()) {
            Genre savedGenre = genreService.updateGenre(existingGenre.get(), updatedGenre);
            return ResponseEntity.ok(savedGenre);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @DeleteMapping("/books/{bookID}/genres/{genreID}")
    public ResponseEntity<HttpStatus> deleteGenreFromBook(@PathVariable(value = "bookID") Long bookID, @PathVariable(value = "genreID") Long genreID) {
        Book book = bookRepository.findById(bookID)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Tutorial with id = " + bookID));

        book.removeGenre(genreID);
        bookRepository.save(book);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @DeleteMapping("/genres/{id}")
    public ResponseEntity<Void> deleteGenreById(@PathVariable("id") Long id) {
        Optional<Genre> genre = genreService.getGenreById(id);
        if (genre.isPresent()) {
            genreService.deleteGenreById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
