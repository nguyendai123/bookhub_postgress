package com.bookhup.service.impl;

import com.bookhup.model.Progress;
import com.bookhup.model.User;
import com.bookhup.model.Book;
import com.bookhup.repository.ProgressRepository;
import com.bookhup.service.ProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProgressServiceImpl implements ProgressService {

    private final ProgressRepository progressRepository;

    @Override
    public List<Progress> getAllProgresses() {
        return progressRepository.findAll();
    }

    @Override
    public Progress save(Progress progress) {
        return progressRepository.save(progress);
    }

    @Override
    public void delete(Progress progress) {
        progressRepository.delete(progress);
    }

    @Override
    public Progress getProgressById(Long progressId) {
        return progressRepository.findById(progressId).orElse(null);
    }

    @Override
    public Progress findByUserProgressAndBook(User user, Book book) {
        return progressRepository.findByUserProgressAndBook(user, book);
    }
}
