package com.bookhup.service.behavior;

import com.bookhup.model.UserBehaviorLog;
import com.bookhup.model.UserFeedWeights;
import com.bookhup.repository.UserFeedWeightsRepository;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class WeightLearningEngine {

    private final UserFeedWeightsRepository repo;
    private final ThreadPoolTaskExecutor weightExecutor;

    public void asyncProcess(UserBehaviorLog log) {
        if (weightExecutor.getThreadPoolExecutor().isShutdown()) return;
        weightExecutor.submit(() -> process(log));
    }

    public void process(UserBehaviorLog log) {
        Long userId = log.getUserId();
        UserFeedWeights w = repo.findById(userId)
                .orElseGet(() -> repo.save(new UserFeedWeights(userId,
                        0.5, 0.3, 0.2)));

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

        w.normalize();
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

