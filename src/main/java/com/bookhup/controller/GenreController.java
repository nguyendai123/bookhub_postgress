package com.bookhup.controller;

import com.bookhup.model.Genre;
import com.bookhup.service.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/genres")
@RequiredArgsConstructor
public class GenreController {
    private final GenreService genreService;

    @PostMapping
    public ResponseEntity<Genre> create(@RequestBody Genre genre) {
        return ResponseEntity.ok(genreService.createGenre(genre));
    }

    @GetMapping
    public ResponseEntity<List<Genre>> getAll() {
        return ResponseEntity.ok(genreService.getAll());
    }
}
