package com.bookhup.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.messaging.core.MessagePostProcessor;

import java.util.List;

@Getter
@AllArgsConstructor
public class NotificationBatchPayload {

    private List<Notification> notifications;
}

