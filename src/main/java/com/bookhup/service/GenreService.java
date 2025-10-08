package com.bookhup.service;

import com.bookhup.repository.GenreRepository;
import com.bookhup.model.Genre;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GenreService {
    @Autowired
    private GenreRepository genreRepository;

    public List<Genre> getAllGenres() {
        return genreRepository.findAll();
    }

    public Optional<Genre> getGenreById(Long id) {
        return genreRepository.findById(id);
    }

    public List<Genre> findByName(String genreName) {
        return genreRepository.findByGenreName(genreName);
    }

    public Genre createGenre(Genre genre) {
        return genreRepository.save(genre);
    }

    public Genre updateGenre(Genre existingGenre, Genre updatedGenre) {
        existingGenre.setGenreName(updatedGenre.getGenreName());
        // Set any other fields you want to update
        return genreRepository.save(existingGenre);
    }

    public void deleteGenreById(Long id) {
        genreRepository.deleteById(id);
    }
}
