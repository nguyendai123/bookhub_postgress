package com.bookhup.service.notification;

import com.bookhup.model.Notification;
import com.bookhup.model.NotificationType;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationBuilder {

    private final NotificationTemplateService templateService;

    public Notification build(NotificationType type, Long targetUserId, Map<String, Object> data, String username) {
        String template = templateService.resolveTemplate(type);
        String finalContent = template.replace("{username}", username);

        return Notification.builder()
                .type(type)
                .userId(targetUserId)
                .priority(NotificationPriorityResolver.priorityOf(type))
                .title(null)
                .content(finalContent)
                .metadata((JsonNode) data)
                .createdAt(LocalDateTime.now())
                .read(false)
                .build();
    }

    private String fillTemplate(String template, Map<String, Object> data) {
        if (data == null) return template;

        String result = template;
        for (var entry : data.entrySet()) {
            String key = entry.getKey();
            String value = String.valueOf(entry.getValue());
            result = result.replace("{" + key + "}", value);
        }
        return result;
    }
}


