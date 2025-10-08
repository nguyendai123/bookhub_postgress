package com.bookhup.service;

import com.bookhup.repository.ProgressRepository;
import com.bookhup.model.Book;
import com.bookhup.model.Progress;
import com.bookhup.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProgressService {

    @Autowired
    private ProgressRepository progressRepository;
    public List<Progress> getAllProgresses() {
        return progressRepository.findAll();
    }
    public Progress save(Progress progress) {
        return progressRepository.save(progress);
    }

    public void delete(Progress progress) {
        progressRepository.delete(progress);
    }

    public Progress getProgressById(Long progressId) {
        return progressRepository.findById(progressId).orElse(null);
    }


    public Progress findByUserProgressAndBook(User userProgress, Book book) {
        return progressRepository.findByUserProgressAndBook(userProgress, book);
    }
}
