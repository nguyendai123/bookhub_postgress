//package com.bookhup.service.impl;
//
//import com.bookhup.model.Follow;
//import com.bookhup.model.User;
//import com.bookhup.repository.FollowRepository;
//import com.bookhup.service.auth.UserService;
//import com.bookhup.service.FollowService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@RequiredArgsConstructor
//@Service
//public class FollowServiceImpl implements FollowService {
//
//    private final FollowRepository followRepository;
//    private final UserService userService;
//
//    @Override
//    public Follow followUser(Long followerId, Long followingId) {
//        User follower = userService.getUserById(followerId);
//        User following = userService.getUserById(followingId);
//
//        Follow follow = new Follow();
//        follow.setFollower(follower);
//        follow.setFollowing(following);
//
//        return followRepository.save(follow);
//    }
//
//    @Override
//    public void unfollowUser(Long followerId, Long followingId) {
//        followRepository.deleteByFollower_UserIDAndFollowing_UserID(followerId, followingId);
//    }
//
//    @Override
//    public List<Follow> getFollowers(Long userId) {
//        return followRepository.findAllByFollowing_UserID(userId);
//    }
//
//    @Override
//    public List<Follow> getFollowing(Long userId) {
//        return followRepository.findAllByFollower_UserID(userId);
//    }
//
//    @Override
//    public boolean isFollowing(Long followerId, Long followingId) {
//        return followRepository.existsByFollower_UserIDAndFollowing_UserID(followerId, followingId);
//    }
//}
//
