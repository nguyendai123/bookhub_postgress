package com.bookhup.service;

import org.springframework.scheduling.annotation.Async;

public interface UserBehaviorLogService {

    @Async("logExecutor")
    void logView(Long userId, Long postId, String source);

    @Async("logExecutor")
    void logLike(Long userId, Long postId, boolean isTrending, boolean isFollowing);

    @Async("logExecutor")
    void logFollow(Long userId, Long followUserId);

    @Async("logExecutor")
    void logUnfollow(Long userId, Long followUserId);
}
