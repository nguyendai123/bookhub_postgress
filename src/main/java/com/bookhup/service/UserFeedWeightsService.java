package com.bookhup.service;

public interface UserFeedWeightsService {
    //Khi user LIKE post
    void adjustWeightsOnLike(Long userId, boolean isFollowingAuthor, boolean isTrendingPost);

    //Khi user xem nhiều bài trending:
    void adjustAfterTrendingViewed(Long userId);

    //Khi user bỏ follow hoặc không xem bài follow
    void adjustAfterUnfollowOrIgnore(Long userId);

    //Khi user chỉ xem bài mới (new / recent)
    void adjustForNewPostsOnly(Long userId);
}
