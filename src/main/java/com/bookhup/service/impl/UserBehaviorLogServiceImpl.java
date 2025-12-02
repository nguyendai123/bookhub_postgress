package com.bookhup.service.impl;

import com.bookhup.model.ActionType;
import com.bookhup.model.UserBehaviorLog;
import com.bookhup.repository.UserBehaviorLogRepository;
import com.bookhup.service.UserBehaviorLogService;
import com.bookhup.service.queue.BehaviorLogQueue;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserBehaviorLogServiceImpl implements UserBehaviorLogService {
    private final UserBehaviorLogRepository userBehaviorLogRepository;

    private final BehaviorLogQueue logQueue;

    @Async("logExecutor")
    @Override
    public void logView(Long userId, Long postId, String source) {
        UserBehaviorLog log = UserBehaviorLog.builder()
                .userId(userId)
                .actionType(ActionType.POST_VIEW)
                .timestamp(LocalDateTime.now())
                .metadata(Map.of(
                        "post_id", postId,
                        "source", source
                ))
                .build();

        logQueue.push(log);
    }

    @Async("logExecutor")
    @Override
    public void logLike(Long userId, Long postId, boolean isTrending, boolean isFollowing) {
        UserBehaviorLog log = UserBehaviorLog.builder()
                .userId(userId)
                .actionType(ActionType.POST_LIKE)
                .timestamp(LocalDateTime.now())
                .metadata(Map.of(
                        "post_id", postId,
                        "is_trending", isTrending,
                        "is_following", isFollowing
                ))
                .build();

        logQueue.push(log);
    }

    @Async("logExecutor")
    @Override
    public void logFollow(Long userId, Long followUserId) {
        logQueue.push(
                UserBehaviorLog.builder()
                        .userId(userId)
                        .actionType(ActionType.USER_FOLLOW)
                        .timestamp(LocalDateTime.now())
                        .metadata(Map.of("follow_user", followUserId))
                        .build()
        );
    }

    @Async("logExecutor")
    @Override
    public void logUnfollow(Long userId, Long followUserId) {
        logQueue.push(
                UserBehaviorLog.builder()
                        .userId(userId)
                        .actionType(ActionType.USER_UNFOLLOW)
                        .timestamp(LocalDateTime.now())
                        .metadata(Map.of("follow_user", followUserId))
                        .build()
        );
    }
}
