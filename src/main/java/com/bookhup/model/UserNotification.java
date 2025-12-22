package com.bookhup.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_notification",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_broadcast",
                        columnNames = {"userId", "broadcastId"}
                )
        },
        indexes = {
                @Index(name = "idx_user_notification_user", columnList = "userId")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long broadcastId;

    private LocalDateTime seenAt;
}

