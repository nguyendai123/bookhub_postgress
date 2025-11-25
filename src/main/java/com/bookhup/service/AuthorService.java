package com.bookhup.service;

import com.bookhup.model.Author;

import java.util.List;

public interface AuthorService {
    Author createAuthor(Author author);

    List<Author> getAll();
}

