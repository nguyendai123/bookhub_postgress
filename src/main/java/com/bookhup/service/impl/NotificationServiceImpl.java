package com.bookhup.service.impl;

import com.bookhup.model.Notification;
import com.bookhup.model.NotificationType;
import com.bookhup.model.User;
import com.bookhup.repository.NotificationRepository;
import com.bookhup.repository.UserRepository;
import com.bookhup.service.NotificationService;
import com.bookhup.service.gateway.WebSocketGateway;
import com.bookhup.service.notification.NotificationBuilder;
import com.bookhup.service.notification.NotificationRuleEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final UserRepository userRepository;
    private final NotificationBuilder builder;
    private final NotificationRuleEngine ruleEngine;
    private final NotificationRepository notificationRepo;
    private final WebSocketGateway webSocketGateway;

    @Override
    public void send(NotificationType type, Long targetUserId, Map<String, Object> data, String username) {
        if (ruleEngine.isAllowed(type, targetUserId)) return;

        Notification noti = builder.build(type, targetUserId, data, username);
        notificationRepo.save(noti);

        ruleEngine.markSent(type, targetUserId);
        // 5. Gửi realtime qua WebSocket
        webSocketGateway.sendNotification(targetUserId, noti);

    }

    @Override
    public void sendNotification(Long userId, String message, NotificationType type) {
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User không tồn tại."));

        Notification n = Notification.builder()
                .userId(userId)
                .content(message)
                .type(type)
                .read(false)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepo.save(n);
    }

    @Override
    public List<Notification> getNotifications(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User không tồn tại."));
        return notificationRepo.findByUserIdOrderByCreatedAtDesc(userId);
    }
}
