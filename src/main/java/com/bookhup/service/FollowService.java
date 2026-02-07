package com.bookhup.service;

import com.bookhup.dto.response.follow.FollowUserDTO;
import com.bookhup.model.Follow;
import com.bookhup.model.User;

import java.util.List;

public interface FollowService {

    void follow(User user, Long targetUserId);

    void unfollow(User user, Long targetUserId);

    List<FollowUserDTO> getFollowers(Long userId);

    List<FollowUserDTO> getFollowing(Long userId);

    boolean isFollowing(Long followerId, Long followingId);
}

