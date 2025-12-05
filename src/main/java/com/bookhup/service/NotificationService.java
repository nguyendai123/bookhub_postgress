package com.bookhup.service;

import com.bookhup.model.Notification;
import com.bookhup.model.NotificationType;

import java.util.List;
import java.util.Map;

public interface NotificationService {

    void send(NotificationType type, Long targetUserId, Map<String, Object> data, String username);

    void sendNotification(Long userId, String message, NotificationType type);

    List<Notification> getNotifications(Long userId);
}
