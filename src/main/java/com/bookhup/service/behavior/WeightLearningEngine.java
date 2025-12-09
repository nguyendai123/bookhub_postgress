package com.bookhup.service.behavior;

import com.bookhup.model.UserBehaviorLog;
import com.bookhup.model.UserFeedWeights;
import com.bookhup.repository.UserFeedWeightsRepository;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WeightLearningEngine {

    private final UserFeedWeightsRepository repo;
    private final ThreadPoolTaskExecutor weightExecutor;

    public void asyncProcess(Long userId, List<UserBehaviorLog> logs) {
        if (weightExecutor.getThreadPoolExecutor().isShutdown()) return;
        weightExecutor.submit(() -> processUser(userId, logs));
    }

    public void processUser(Long userId, List<UserBehaviorLog> logs) {
        // lấy weight hiện tại
        UserFeedWeights w = repo.findById(userId)
                .orElseGet(() -> new UserFeedWeights(userId, 0.5, 0.3, 0.2));

        // xử lý tất cả logs cho user đó
        for (UserBehaviorLog log : logs) {
            switch (log.getActionType()) {

                case POST_VIEW -> handlePostView(log, w);
                case POST_LIKE -> handlePostLike(log, w);

                case USER_FOLLOW -> {
                    w.setWFollowing(w.getWFollowing() + 0.02);
                    w.setLastUpdate(LocalDateTime.now());
                }

                case USER_UNFOLLOW -> {
                    w.setWFollowing(w.getWFollowing() - 0.03);
                    w.setLastUpdate(LocalDateTime.now());
                }
            }
        }

        // normalize 1 lần
        w.normalize();

        // lưu 1 lần
        repo.save(w);
    }

    private void handlePostView(UserBehaviorLog log, UserFeedWeights w) {
        String source = (String) log.getMetadata().get("source");
        w.setLastUpdate(LocalDateTime.now());
        if ("trending".equals(source)) {
            w.setWTrending(w.getWTrending() + 0.03);
        } else if ("following_feed".equals(source)) {
            w.setWFollowing(w.getWFollowing() + 0.02);
        } else if ("new_feed".equals(source)) {
            w.setWRecentInteraction(w.getWRecentInteraction() + 0.02);
            w.setWTrending(w.getWTrending() - 0.005);
        }
    }

    private void handlePostLike(UserBehaviorLog log, UserFeedWeights w) {
        Boolean trending = (Boolean) log.getMetadata().get("is_trending");
        Boolean following = (Boolean) log.getMetadata().get("is_following");
        w.setLastUpdate(LocalDateTime.now());
        w.setWRecentInteraction(w.getWRecentInteraction() + 0.01);

        if (Boolean.TRUE.equals(trending)) {
            w.setWTrending(w.getWTrending() + 0.02);
        }
        if (Boolean.TRUE.equals(following)) {
            w.setWFollowing(w.getWFollowing() + 0.02);
        }
    }

    @PreDestroy
    public void shutdown() {
        System.out.println("Shutting down WeightLearningEngine...");
        weightExecutor.shutdown(); // Spring sẽ chờ tasks finish
        System.out.println("WeightLearningEngine stopped.");
    }
}

