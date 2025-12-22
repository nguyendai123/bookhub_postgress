package com.bookhup.service.notification;

import com.bookhup.model.BroadcastNotification;
import com.bookhup.model.Notification;
import com.bookhup.model.NotificationType;
import com.bookhup.model.UserBehaviorLog;
import com.bookhup.repository.BroadcastNotificationRepository;
import com.bookhup.repository.NotificationRepository;
import com.bookhup.service.gateway.WebSocketGateway;
import com.bookhup.service.mapper.BehaviorToNotificationMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;

@Service
@RequiredArgsConstructor
public class NotificationBatchWorker {

    private static final Logger logger = LoggerFactory.getLogger(NotificationBatchWorker.class);
    private final LinkedBlockingQueue<UserBehaviorLog> queue = new LinkedBlockingQueue<>(5000);
    private static final int FANOUT_THRESHOLD = 100_000;


    private final BehaviorToNotificationMapper mapper;
    private final NotificationBuilder notificationBuilder;
    private final NotificationRepository notificationRepo;
    private final BroadcastNotificationRepository broadcastRepo;
    private final NotificationRuleEngine ruleEngine;
    private final WebSocketGateway webSocketGateway;

    private final ThreadPoolTaskExecutor notificationExecutor;
    private final ThreadPoolTaskExecutor fanoutExecutor;

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

//                Long targetUserId = extractTargetUser(log);
//                if (ruleEngine.isAllowed(type, targetUserId)) continue;
//
//                Map<String, Object> data = log.getMetadata();
//
//                Notification n = notificationBuilder.build(type, targetUserId, data, log.getUsername());
//                if (n == null) continue;
//                buffer.add(n);
//
//                // Batch save
//                if (buffer.size() >= batchSize) {
//                    flush(buffer);
//                }
//
//                // Realtime push
//                webSocketGateway.sendNotification(targetUserId, n);
//
//                ruleEngine.markSent(type, targetUserId);
                switch (log.getTargetType()) {

                    case SELF -> handleOne(log.getUserId(), log, buffer);

                    case SINGLE_USER -> handleOne(log.getTargetUserId(), log, buffer);

                    case MULTI_USERS -> {
                        int size = log.getTargetUserIds().size();

                        if (size <= FANOUT_THRESHOLD) {
                            fanoutSmall(log, type);
                        } else {
                            fanoutLarge(log);
                        }
                    }


                    case ALL_USERS -> broadcast(log);

                    case NONE -> {
                        // chỉ log, không gửi noti
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // flush cuối
        flush(buffer);
    }

    private void broadcast(UserBehaviorLog log) {
        fanoutLarge(log);
    }

    private void fanoutSmall(UserBehaviorLog log, NotificationType type) {

        List<Long> users = new ArrayList<>(log.getTargetUserIds());
        int shardSize = 1000;

        for (int i = 0; i < users.size(); i += shardSize) {

            List<Long> shard = users.subList(
                    i,
                    Math.min(i + shardSize, users.size())
            );

            try {
                fanoutExecutor.execute(() -> {
                    try {
                        handleShard(shard, log, type);
                    } catch (Exception e) {
                        logger.error("Fanout shard failed", e);
                    }
                });
            } catch (RejectedExecutionException ex) {
                // 🔥 BACKPRESSURE – fallback xử lý trực tiếp
                handleShard(shard, log, type);
            }
        }
    }


    private void handleShard(List<Long> userIds, UserBehaviorLog log, NotificationType type) {

        List<Notification> batch = new ArrayList<>(userIds.size());

        for (Long uid : userIds) {

            if (ruleEngine.isAllowed(type, uid)) continue;

            Notification n = notificationBuilder.build(
                    mapper.map(log.getActionType()),
                    uid,
                    log.getMetadata(),
                    log.getUsername()
            );

            if (n != null) batch.add(n);
        }

        if (!batch.isEmpty()) {
            notificationRepo.saveAll(batch);   // batch insert
            webSocketGateway.pushBatch(batch); // batch WS
        }
    }

    private void fanoutLarge(UserBehaviorLog log) {

        BroadcastNotification broadcast = BroadcastNotification.builder()
                .type(mapper.map(log.getActionType()))
                .actorUserId(log.getUserId())
                .metadata(log.getMetadata())
                .createdAt(LocalDateTime.now())
                .build();

        broadcastRepo.save(broadcast);
    }


    private void handleOne(Long targetUserId,
                           UserBehaviorLog log,
                           List<Notification> buffer) {

        if (ruleEngine.isAllowed(mapper.map(log.getActionType()), targetUserId)) {
            return;
        }

        Notification n = notificationBuilder.build(
                mapper.map(log.getActionType()),
                targetUserId,
                log.getMetadata(),
                log.getUsername()
        );

        if (n == null) return;

        buffer.add(n);

        if (buffer.size() >= batchSize) {
            flush(buffer);
        }

        webSocketGateway.sendNotification(targetUserId, n);
        ruleEngine.markSent(mapper.map(log.getActionType()), targetUserId);
    }


    private void flush(List<Notification> buffer) {
        if (buffer.isEmpty()) return;

        List<Notification> copy = new ArrayList<>(buffer);
        buffer.clear();

        notificationRepo.saveAll(copy);
        System.out.println("Flushed " + copy.size() + " notifications");
    }

    private Long extractTargetUser(UserBehaviorLog log) {
        return log.getTargetUserId();
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

