package com.bookhup.controller.service;

import com.bookhup.model.Follow;
import com.bookhup.model.User;
import com.bookhup.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DevFollowService {

    private final FollowRepository followRepository;

    @Transactional
    public void generateFollows(List<User> users) {

        Random random = new Random();

        List<Follow> followBatch = new ArrayList<>();

        // Map để cộng dồn counter
        Map<Long, Integer> followersMap = new HashMap<>();
        Map<Long, Integer> followingMap = new HashMap<>();

        for (User user : users) {

            int followTimes = random.nextInt(20);

            Set<Long> followedIds = new HashSet<>();

            for (int i = 0; i < followTimes; i++) {

                User target = users.get(random.nextInt(users.size()));

                if (user.getUserId().equals(target.getUserId()))
                    continue;

                if (!followedIds.add(target.getUserId()))
                    continue;

                // Check tồn tại để tránh trùng DB
                boolean existed = followRepository
                        .existsByUserAndFollowUser(
                                user,
                                target
                        );

                if (existed) continue;

                Follow follow = Follow.builder()
                        .user(user)
                        .followUser(target)
                        .ownerId(user.getUserId())
                        .createdAt(LocalDateTime.now())
                        .build();

                followBatch.add(follow);

                // cộng dồn counter
                followingMap.merge(user.getUserId(), 1, Integer::sum);
                followersMap.merge(target.getUserId(), 1, Integer::sum);
            }
        }

        // 🔥 Batch insert
        followRepository.saveAll(followBatch);

    }

}

