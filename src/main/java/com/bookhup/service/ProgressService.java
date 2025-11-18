package com.bookhup.service;

import com.bookhup.model.Progress;
import com.bookhup.model.User;
import com.bookhup.model.Book;

import java.util.List;

public interface ProgressService {

    List<Progress> getAllProgresses();

    Progress save(Progress progress);

    void delete(Progress progress);

    Progress getProgressById(Long progressId);

    Progress findByUserProgressAndBook(User user, Book book);
}

