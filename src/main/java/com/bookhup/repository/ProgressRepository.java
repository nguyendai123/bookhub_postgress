package com.bookhup.repository;


import com.bookhup.model.Book;
import com.bookhup.model.Progress;
import com.bookhup.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProgressRepository extends JpaRepository<Progress, Long> {



    Progress findByUserProgressAndBook(User userProgress, Book book);
}
