package com.bookhup.repository;

import com.bookhup.model.NotificationRateLimit;
import com.bookhup.model.NotificationRateLimitId;
import com.bookhup.model.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRateLimitRepository extends JpaRepository<NotificationRateLimit, NotificationRateLimitId> {
    NotificationRateLimit findByUserIdAndType(Long userId, NotificationType type);
}

