package com.bookhup.service.notification;

import com.bookhup.model.Notification;
import com.bookhup.model.NotificationType;
import com.bookhup.model.UserBehaviorLog;
import com.bookhup.repository.NotificationRepository;
import com.bookhup.service.gateway.WebSocketGateway;
import com.bookhup.service.mapper.BehaviorToNotificationMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

@Service
@RequiredArgsConstructor
public class NotificationBatchWorker {

    private final LinkedBlockingQueue<UserBehaviorLog> queue = new LinkedBlockingQueue<>(5000);

    private final BehaviorToNotificationMapper mapper;
    private final NotificationBuilder notificationBuilder;
    private final NotificationRepository notificationRepo;
    private final NotificationRuleEngine ruleEngine;
    private final WebSocketGateway webSocketGateway;

    private final ThreadPoolTaskExecutor notificationExecutor;

    @Value("${notification-push.workers:1}")
    private int workers;

    @Value("${notification-push.batch-size:2}")
    private int batchSize;

    private volatile boolean running = true;

    private static final UserBehaviorLog POISON = new UserBehaviorLog();

    @PostConstruct
    public void start() {
        for (int i = 0; i < workers; i++) {
            notificationExecutor.submit(this::workerLoop);
        }
    }

    public void submit(UserBehaviorLog log) {
        queue.offer(log);
    }

    private void workerLoop() {
        List<Notification> buffer = new ArrayList<>(batchSize);

        while (running) {
            try {
                UserBehaviorLog log = queue.take();

                if (log == POISON) break;  // Shutdown worker

                NotificationType type = mapper.map(log.getActionType());
                if (type == null) continue;

                Long targetUserId = extractTargetUser(log);
                if (ruleEngine.isAllowed(type, targetUserId)) continue;

                Map<String, Object> data = log.getMetadata();

                Notification n = notificationBuilder.build(type, targetUserId, data, log.getUsername());
                buffer.add(n);

                // Batch save
                if (buffer.size() >= batchSize) {
                    flush(buffer);
                }

                // Realtime push
                webSocketGateway.sendNotification(targetUserId, n);

                ruleEngine.markSent(type, targetUserId);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // flush cuối
        flush(buffer);
    }

    private void flush(List<Notification> buffer) {
        if (buffer.isEmpty()) return;

        List<Notification> copy = new ArrayList<>(buffer);
        buffer.clear();

        notificationRepo.saveAll(copy);
        System.out.println("Flushed " + copy.size() + " notifications");
    }

    private Long extractTargetUser(UserBehaviorLog log) {
        return Long.parseLong(log.getMetadata().get("targetUserId").toString());
    }

    @PreDestroy
    public void shutdown() {
        System.out.println("Shutting down NotificationBatchWorker...");

        running = false;

        // Gửi POISON để cho các worker break vòng while
        for (int i = 0; i < workers; i++) {
            queue.offer(POISON);
        }

        // Đợi executor shutdown
        notificationExecutor.shutdown();

        // Cuối cùng flush tất cả log còn trong queue
        List<UserBehaviorLog> leftover = new ArrayList<>();
        queue.drainTo(leftover);

        if (!leftover.isEmpty()) {
            System.out.println("Final leftover: " + leftover.size());

            List<Notification> finalNoti = new ArrayList<>();

            for (UserBehaviorLog log : leftover) {
                NotificationType type = mapper.map(log.getActionType());
                if (type == null) continue;

                Long targetUserId = extractTargetUser(log);

                if (ruleEngine.isAllowed(type, targetUserId)) continue;

                Notification n = notificationBuilder.build(type, targetUserId, log.getMetadata(), log.getUsername());
                finalNoti.add(n);
            }

            if (!finalNoti.isEmpty()) {
                notificationRepo.saveAll(finalNoti);
                System.out.println("Final flush saved " + finalNoti.size() + " notifications");
            }
        }

        System.out.println("NotificationBatchWorker stopped safely.");
    }
}

