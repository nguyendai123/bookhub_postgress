package com.bookhup.service.impl;

import com.bookhup.model.Genre;
import com.bookhup.repository.GenreRepository;
import com.bookhup.service.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;

    @Override
    public List<Genre> getAllGenres() {
        return genreRepository.findAll();
    }

    @Override
    public Optional<Genre> getGenreById(Long id) {
        return genreRepository.findById(id);
    }

    @Override
    public List<Genre> findByName(String genreName) {
        return genreRepository.findByGenreName(genreName);
    }

    @Override
    public Genre createGenre(Genre genre) {
        return genreRepository.save(genre);
    }

    @Override
    public Genre updateGenre(Genre existingGenre, Genre updatedGenre) {
        existingGenre.setGenreName(updatedGenre.getGenreName());
        // Cập nhật các trường khác nếu cần
        return genreRepository.save(existingGenre);
    }

    @Override
    public void deleteGenreById(Long id) {
        genreRepository.deleteById(id);
    }
}
