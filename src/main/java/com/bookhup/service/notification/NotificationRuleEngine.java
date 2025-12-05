package com.bookhup.service.notification;

import com.bookhup.model.NotificationPriority;
import com.bookhup.model.NotificationRateLimit;
import com.bookhup.model.NotificationType;
import com.bookhup.repository.NotificationRateLimitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class NotificationRuleEngine {

    private final NotificationRateLimitRepository rateRepo;

    public boolean isAllowed(NotificationType type, Long userId) {
        NotificationRateLimit rl = rateRepo.findByUserIdAndType(userId, type);

        NotificationPriority pri = NotificationPriorityResolver.priorityOf(type);

        Duration limit = switch (pri) {
            case LOWEST -> Duration.ofHours(12);
            case LOW -> Duration.ofHours(2);
            case MEDIUM -> Duration.ofHours(1).dividedBy(3);
            default -> Duration.ZERO; // HIGH + HIGHEST không limit
        };

        if (limit.isZero()) return false;

        if (rl == null) return false;

        return Duration.between(rl.getLastSentAt(), LocalDateTime.now()).compareTo(limit) <= 0;
    }

    public void markSent(NotificationType type, Long userId) {
        NotificationRateLimit rl =
                rateRepo.findByUserIdAndType(userId, type);

        if (rl == null) {
            rl = NotificationRateLimit.builder()
                    .userId(userId)
                    .type(type)
                    .lastSentAt(LocalDateTime.now())
                    .countInPeriod(1)
                    .build();
        } else {
            rl.setLastSentAt(LocalDateTime.now());
            rl.setCountInPeriod(rl.getCountInPeriod() + 1);
        }
        rateRepo.save(rl);
    }
}

