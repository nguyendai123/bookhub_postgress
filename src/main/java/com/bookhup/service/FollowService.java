package com.bookhup.service;

import com.bookhup.model.Follow;
import java.util.List;

public interface FollowService {

    Follow followUser(Long followerId, Long followingId);

    void unfollowUser(Long followerId, Long followingId);

    List<Follow> getFollowers(Long userId);

    List<Follow> getFollowing(Long userId);

    boolean isFollowing(Long followerId, Long followingId);
}

