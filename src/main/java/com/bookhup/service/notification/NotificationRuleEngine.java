package com.bookhup.service.notification;

import com.bookhup.model.*;
import com.bookhup.repository.NotificationRateLimitRepository;
import com.bookhup.repository.NotificationRepository;
import com.bookhup.repository.UserStatsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class NotificationRuleEngine {

    private final NotificationRateLimitRepository rateRepo;
    private final UserStatsRepository userStatsRepository;
    private final NotificationRepository notificationRepo;

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

        // 3. DAILY LIMIT theo từng user
        UserStats userLimit = userStatsRepository.findByUserId(userId);
        int dailyLimit = (userLimit != null && userLimit.getDailyLimit() != null)
                ? userLimit.getDailyLimit()
                : 10; // default nếu user không config

        // đếm số thông báo đã gửi trong ngày
        LocalDate today = LocalDate.now();

        int countToday = notificationRepo.countByUserIdAndCreatedAtBetween(
                userId,
                today.atStartOfDay(),
                today.plusDays(1).atStartOfDay()
        );

        // Nếu vượt quá hạn mức được cấu hình hoac nho hon gio toi thieu thi khong gui noti

        return countToday > dailyLimit || Duration.between(rl.getLastSentAt(), LocalDateTime.now()).compareTo(limit) <= 0;
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

