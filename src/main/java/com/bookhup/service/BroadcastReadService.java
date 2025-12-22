package com.bookhup.service;

import com.bookhup.model.Notification;

import java.time.LocalDateTime;
import java.util.List;

public interface BroadcastReadService {
    public List<Notification> fetchBroadcastNotifications(Long userId, LocalDateTime lastSeen);
}
