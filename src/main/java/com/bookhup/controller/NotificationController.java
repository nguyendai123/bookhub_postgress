package com.bookhup.controller;

import com.bookhup.model.Notification;
import com.bookhup.model.User;
import com.bookhup.service.BroadcastReadService;
import com.bookhup.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final BroadcastReadService broadcastReadService;

    @GetMapping
    public List<Notification> getNotifications(@RequestAttribute("currentUser") User user) {
        return notificationService.getNotifications(user.getUserId());
    }

    @GetMapping("/follower/broadcast")
    public List<Notification> getBroadcastNotifications(
            @RequestParam(required = false) LocalDateTime lastSeen,
            @RequestAttribute("currentUser") User user
    ) {
        if (lastSeen == null) {
            lastSeen = LocalDateTime.now().minusDays(7);
        }

        return broadcastReadService
                .fetchBroadcastNotifications(user.getUserId(), lastSeen);
    }

}

