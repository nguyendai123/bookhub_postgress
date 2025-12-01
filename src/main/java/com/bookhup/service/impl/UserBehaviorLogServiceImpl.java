package com.bookhup.service.impl;

import com.bookhup.model.ActionType;
import com.bookhup.model.UserBehaviorLog;
import com.bookhup.repository.UserBehaviorLogRepository;
import com.bookhup.service.UserBehaviorLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

import static com.bookhup.model.ActionType.POST_LIKE;
import static com.bookhup.model.ActionType.POST_VIEW;

@Service
@RequiredArgsConstructor
public class UserBehaviorLogServiceImpl implements UserBehaviorLogService {
    private final UserBehaviorLogRepository userBehaviorLogRepository;

    @Override
    public void logView(Long userId, Long postId, String device, String location) {
        UserBehaviorLog log = UserBehaviorLog.builder()
                .userId(userId)
                .actionType(POST_VIEW)
                .device(device)
                .location(location)
                .timestamp(LocalDateTime.now())
                .metadata(Map.of("post_id", postId))
                .build();

        userBehaviorLogRepository.save(log);
    }

    @Override
    public void logLike(Long userId, Long postId) {
        UserBehaviorLog log = UserBehaviorLog.builder()
                .userId(userId)
                .actionType(POST_LIKE)
                .timestamp(LocalDateTime.now())
                .metadata(Map.of("post_id", postId))
                .build();

        userBehaviorLogRepository.save(log);
    }

    @Override
    public void logFollow(Long userId, Long targetUserId, ActionType type) {
        UserBehaviorLog log = UserBehaviorLog.builder()
                .userId(userId)
                .actionType(type)
                .timestamp(LocalDateTime.now())
                .metadata(Map.of("target_user_id", targetUserId))
                .build();

        userBehaviorLogRepository.save(log);
    }


}
