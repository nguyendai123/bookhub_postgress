package com.bookhup.service;

import com.bookhup.model.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreService {
    Genre createGenre(Genre genre);

    List<Genre> getAll();
}
