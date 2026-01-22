package com.bookhup.repository;

import com.bookhup.model.AIInteraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AIInteractionRepository extends JpaRepository<AIInteraction, Long> {
    List<AIInteraction> findByUser_UserIdAndBook_BookIdOrderByCreatedAtAsc(
            Long userId,
            Long bookId
    );
}
