package com.bookhup.service.impl;

import com.bookhup.model.Author;
import com.bookhup.repository.AuthorRepository;
import com.bookhup.service.AuthorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    @Override
    public Author createAuthor(Author author) {
        if (authorRepository.findByName(author.getName()).isPresent()) {
            throw new RuntimeException("Author already exists");
        }
        return authorRepository.save(author);
    }

    @Override
    public List<Author> getAll() {
        return authorRepository.findAll();
    }
}

