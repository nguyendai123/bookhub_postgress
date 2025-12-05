package com.bookhup.service;

import com.bookhup.model.Notification;
import com.bookhup.model.NotificationType;

import java.util.List;
import java.util.Map;

public interface NotificationService {

    void send(NotificationType type, Long userId, Map<String, Object> data);

    void sendNotification(Long userId, String message, NotificationType type);

    List<Notification> getNotifications(Long userId);
}
