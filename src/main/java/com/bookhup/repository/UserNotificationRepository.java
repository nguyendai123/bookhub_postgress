package com.bookhup.repository;

import com.bookhup.model.UserNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserNotificationRepository extends JpaRepository<UserNotification, Long> {

    boolean existsByUserIdAndBroadcastId(Long userId, Long broadcastId);
}

