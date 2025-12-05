package com.bookhup.service.notification;

import com.bookhup.model.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationTemplateService {

    private final NotificationTemplateConfig config;

    public String resolveTemplate(NotificationType type) {
        return config.getTemplates().get(type.name());
    }
}
