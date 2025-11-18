package com.bookhup.service;

import com.bookhup.model.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreService {

    List<Genre> getAllGenres();

    Optional<Genre> getGenreById(Long id);

    List<Genre> findByName(String genreName);

    Genre createGenre(Genre genre);

    Genre updateGenre(Genre existingGenre, Genre updatedGenre);

    void deleteGenreById(Long id);
}
