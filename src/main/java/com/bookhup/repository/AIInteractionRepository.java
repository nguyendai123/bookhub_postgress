package com.bookhup.repository;

import com.bookhup.model.AIInteraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AIInteractionRepository extends JpaRepository<AIInteraction, Long> {
}
