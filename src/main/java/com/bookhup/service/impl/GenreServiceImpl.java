package com.bookhup.service.impl;

import com.bookhup.model.Genre;
import com.bookhup.repository.GenreRepository;
import com.bookhup.service.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;

    @Override
    public Genre createGenre(Genre genre) {
        if (genreRepository.findByName(genre.getName()).isPresent()) {
            throw new RuntimeException("Genre already exists");
        }
        return genreRepository.save(genre);
    }

    @Override
    public List<Genre> getAll() {
        return genreRepository.findAll();
    }
}