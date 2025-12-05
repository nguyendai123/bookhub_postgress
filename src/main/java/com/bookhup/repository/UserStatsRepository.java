package com.bookhup.repository;

import com.bookhup.model.Author;
import com.bookhup.model.UserStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserStatsRepository extends JpaRepository<UserStats, Long> {
    UserStats findByUserId(Long userId);
}

