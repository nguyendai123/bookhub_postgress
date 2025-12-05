package com.bookhup.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification_rate_limit")
@IdClass(NotificationRateLimitId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRateLimit {

    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 50, nullable = false)
    private NotificationType type;

    @Column(name = "last_sent_at")
    private LocalDateTime lastSentAt;

    @Column(name = "count_in_period")
    private Integer countInPeriod;
}

