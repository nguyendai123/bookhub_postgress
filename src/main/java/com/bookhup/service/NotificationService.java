package com.bookhup.service;

import com.bookhup.model.Notification;

import java.util.List;

public interface NotificationService {

    void sendNotification(Long userId, String message, String type);

    List<Notification> getNotifications(Long userId);
}
