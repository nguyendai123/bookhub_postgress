package com.bookhup.service.impl;

import com.bookhup.model.BroadcastNotification;
import com.bookhup.model.Notification;
import com.bookhup.model.UserNotification;
import com.bookhup.repository.BroadcastNotificationRepository;
import com.bookhup.repository.UserNotificationRepository;
import com.bookhup.service.BroadcastReadService;
import com.bookhup.service.notification.NotificationBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BroadcastReadServiceImpl implements BroadcastReadService {

    private final BroadcastNotificationRepository broadcastRepo;
    private final UserNotificationRepository userNotificationRepo;
    private final NotificationBuilder notificationBuilder;

    @Transactional
    @Override
    public List<Notification> fetchBroadcastNotifications(
            Long userId,
            LocalDateTime lastSeen
    ) {

        List<BroadcastNotification> broadcasts =
                broadcastRepo.findUnreadBroadcasts(
                        userId,
                        lastSeen,
                        PageRequest.of(0, 50)
                );

        List<Notification> result = new ArrayList<>();

        for (BroadcastNotification b : broadcasts) {

            Notification n = notificationBuilder.build(
                    b.getType(),
                    userId,
                    b.getMetadata(),
                    null   // system / actor name optional
            );

            if (n == null) continue;

            result.add(n);

            // ✅ Đánh dấu đã nhận
            userNotificationRepo.save(
                    UserNotification.builder()
                            .userId(userId)
                            .broadcastId(b.getId())
                            .seenAt(LocalDateTime.now())
                            .build()
            );
        }

        return result;
    }
}
