package com.bookhup.service.notification;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "notification")
@Data
public class NotificationTemplateConfig {
    private Map<String, String> templates;
}
