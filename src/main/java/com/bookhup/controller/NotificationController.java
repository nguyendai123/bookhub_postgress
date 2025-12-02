package com.bookhup.controller;

import com.bookhup.model.Notification;
import com.bookhup.model.User;
import com.bookhup.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public List<Notification> getNotifications(@RequestAttribute("currentUser") User user) {
        return notificationService.getNotifications(user.getUserId());
    }
}

