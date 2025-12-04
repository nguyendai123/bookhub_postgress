package com.bookhup.service.impl;

import com.bookhup.model.Follow;
import com.bookhup.model.User;
import com.bookhup.repository.FollowRepository;
import com.bookhup.repository.UserRepository;
import com.bookhup.service.FollowService;
import com.bookhup.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final NotificationService notificationService;

    @Override
    public void follow(User user, Long targetUserId) {

        if (user.getUserId().equals(targetUserId))
            throw new RuntimeException("Không thể tự follow chính mình.");

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
    }

    @Override
    public void unfollow(User user, Long targetUserId) {
        User target = userRepository.findById(targetUserId)
                .orElseThrow(() -> new RuntimeException("Người được unfollow không tồn tại."));

        Follow follow = followRepository.findByUserAndFollowUser(user, target)
                .orElseThrow(() -> new RuntimeException("Bạn chưa follow người này."));

        followRepository.delete(follow);
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
