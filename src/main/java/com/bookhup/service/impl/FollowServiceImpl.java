package com.bookhup.service.impl;

import com.bookhup.model.*;
import com.bookhup.repository.FollowRepository;
import com.bookhup.repository.UserRepository;
import com.bookhup.service.FollowService;
import com.bookhup.service.NotificationService;
import com.bookhup.service.UserBehaviorLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.bookhup.model.ActionType.USER_FOLLOW;
import static com.bookhup.model.ActionType.USER_UNFOLLOW;

@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final NotificationService notificationService;
    private final UserBehaviorLogService behaviorLogService;

    @Override
    public void follow(Long userId, Long targetUserId) {

        if (userId.equals(targetUserId))
            throw new RuntimeException("Không thể tự follow chính mình.");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User không tồn tại."));

        User target = userRepository.findById(targetUserId)
                .orElseThrow(() -> new RuntimeException("Người được follow không tồn tại."));

        if (followRepository.existsByUserAndFollowUser(user, target))
            throw new RuntimeException("Bạn đã follow người này rồi.");

        Follow follow = Follow.builder()
                .user(user)
                .followUser(target)
                .createdAt(LocalDateTime.now())
                .build();

        followRepository.save(follow);

        // Gửi thông báo
        notificationService.sendNotification(
                targetUserId,
                user.getUsername() + " đã follow bạn.",
                "FOLLOW"
        );

        // Ghi log hành vi
        behaviorLogService.logFollow(userId, targetUserId, USER_FOLLOW);
    }

    @Override
    public void unfollow(Long userId, Long targetUserId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User không tồn tại."));

        User target = userRepository.findById(targetUserId)
                .orElseThrow(() -> new RuntimeException("Người được unfollow không tồn tại."));

        Follow follow = followRepository.findByUserAndFollowUser(user, target)
                .orElseThrow(() -> new RuntimeException("Bạn chưa follow người này."));

        followRepository.delete(follow);

        // Ghi log hành vi
        behaviorLogService.logFollow(userId, targetUserId, USER_UNFOLLOW);
    }

    @Override
    public List<Follow> getFollowers(Long userId) {
        User target = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User không tồn tại."));

        return followRepository.findByFollowUser(target);
    }

    @Override
    public List<Follow> getFollowing(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User không tồn tại."));

        return followRepository.findByUser(user);
    }

    @Override
    public boolean isFollowing(Long followerId, Long followingId) {
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new RuntimeException("Follower không tồn tại."));

        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new RuntimeException("User được theo dõi không tồn tại."));

        return followRepository.existsByUserAndFollowUser(follower, following);
    }
}
