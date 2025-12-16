package com.bookhup.service.notification;

import com.bookhup.model.Notification;
import com.bookhup.model.NotificationType;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationBuilder {

    private final NotificationTemplateService templateService;
    private static final Logger log = LoggerFactory.getLogger(NotificationBuilder.class);

    public Notification build(NotificationType type, Long targetUserId, Map<String, Object> data, String username) {
        String template = templateService.resolveTemplate(type);
        if (template == null || template.isBlank()) {
            log.warn(
                    "[Notification] No template found for type={} | targetUserId={} | data={}",
                    type, targetUserId, data
            );
            return null; // KHÔNG build notification
        }
        String finalContent = template.replace("{username}", username);
        if (!template.contains("{username}")) {
            log.warn("[Notification] Template missing {username} placeholder | type={}", type);
        }
        return Notification.builder()
                .type(type)
                .userId(targetUserId)
                .priority(NotificationPriorityResolver.priorityOf(type))
                .title(null)
                .content(finalContent)
                .metadata(data)
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


