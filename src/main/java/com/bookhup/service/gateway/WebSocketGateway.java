package com.bookhup.service.gateway;

import com.bookhup.model.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Service
@RequiredArgsConstructor
public class WebSocketGateway {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendNotification(Long userId, Notification noti) {
        String destination = "/topic/user/" + userId + "/notifications";
        messagingTemplate.convertAndSend(destination, noti);
    }
}