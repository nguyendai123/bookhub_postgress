package com.bookhup.service.impl;

import com.bookhup.model.Notification;
import com.bookhup.model.User;
import com.bookhup.repository.NotificationRepository;
import com.bookhup.repository.UserRepository;
import com.bookhup.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository repository;
    private final UserRepository userRepository;

    @Override
    public void sendNotification(Long userId, String message, String type) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User không tồn tại."));

        Notification n = Notification.builder()
                .user(user)
                .message(message)
                .type(type)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        repository.save(n);
    }

    @Override
    public List<Notification> getNotifications(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User không tồn tại."));
        return repository.findByUserOrderByCreatedAtDesc(user);
    }
}
