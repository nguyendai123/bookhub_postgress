package com.bookhup.model;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRateLimitId implements Serializable {

    private Long userId;

    @Enumerated(EnumType.STRING)
    private NotificationType type;
}

