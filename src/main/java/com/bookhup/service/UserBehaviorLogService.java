package com.bookhup.service;

import com.bookhup.model.ActionType;

public interface UserBehaviorLogService {
    void logView(Long userId, Long postId, String device, String location);

    void logLike(Long userId, Long postId);

    void logFollow(Long userId, Long targetUserId, ActionType type);
}
