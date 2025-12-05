package com.bookhup.repository;

import com.bookhup.model.Notification;
import com.bookhup.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    int countByUserIdAndCreatedAtBetween(Long userId, LocalDateTime localDateTime, LocalDateTime localDateTime1);
}

