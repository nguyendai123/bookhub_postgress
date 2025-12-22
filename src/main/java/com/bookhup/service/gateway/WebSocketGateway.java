package com.bookhup.service.gateway;

import com.bookhup.model.Notification;
import com.bookhup.model.NotificationBatchPayload;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WebSocketGateway {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketGateway.class);
    private final SimpMessagingTemplate messagingTemplate;
    private static final String DEST_PREFIX = "/topic/notifications/";

    /**
     * Push 1 batch notification cho nhiều user
     */
    public void pushBatch(List<Notification> batch) {

        if (batch == null || batch.isEmpty()) return;

        // group theo userId
        Map<Long, List<Notification>> byUser =
                batch.stream()
                        .collect(Collectors.groupingBy(Notification::getUserId));

        byUser.forEach((userId, notifications) -> {
            try {
                messagingTemplate.convertAndSend(
                        DEST_PREFIX + userId,
                        new NotificationBatchPayload(notifications)
                );
            } catch (Exception e) {
                // ❌ KHÔNG throw để chết worker
                logger.warn("WS push failed for user {}", userId, e);
            }
        });
    }

    public void sendNotification(Long userId, Notification noti) {
        String destination = "/topic/notifications/" + userId;
        messagingTemplate.convertAndSend(destination, noti);
    }
}