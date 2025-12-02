package com.bookhup.service;

import com.bookhup.model.Follow;
import com.bookhup.model.User;

import java.util.List;

public interface FollowService {

    void follow(User user, Long targetUserId);

    void unfollow(User user, Long targetUserId);

    List<Follow> getFollowers(Long userId);

    List<Follow> getFollowing(Long userId);

    boolean isFollowing(Long followerId, Long followingId);
}

