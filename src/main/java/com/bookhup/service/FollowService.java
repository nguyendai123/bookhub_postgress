package com.bookhup.service;

import com.bookhup.model.Follow;
import java.util.List;

public interface FollowService {

    void follow(Long userId, Long targetUserId);

    void unfollow(Long userId, Long targetUserId);

    List<Follow> getFollowers(Long userId);

    List<Follow> getFollowing(Long userId);

    boolean isFollowing(Long followerId, Long followingId);
}

