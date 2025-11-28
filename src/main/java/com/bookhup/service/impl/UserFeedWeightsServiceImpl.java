package com.bookhup.service.impl;

import com.bookhup.model.UserFeedWeights;
import com.bookhup.repository.UserFeedWeightsRepository;
import com.bookhup.service.UserFeedWeightsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserFeedWeightsServiceImpl implements UserFeedWeightsService {
    private final UserFeedWeightsRepository weightsRepo;

    //Khi user LIKE post
    public void adjustWeightsOnLike(Long userId,
                                    boolean isFollowingAuthor,
                                    boolean isTrendingPost) {

        UserFeedWeights w = weightsRepo.findById(userId).orElseThrow();

        if (isFollowingAuthor) {
            w.setWFollowing(w.getWFollowing() + 0.02);
        }

        if (isTrendingPost) {
            w.setWTrending(w.getWTrending() + 0.02);
        }

        // luôn tăng interaction
        w.setWRecentInteraction(w.getWRecentInteraction() + 0.01);

        w.normalize();
        weightsRepo.save(w);
    }

    //Khi user xem nhiều bài trending:
    public void adjustAfterTrendingViewed(Long userId) {
        UserFeedWeights w = weightsRepo.findById(userId).orElseThrow();
        w.setWTrending(w.getWTrending() + 0.03);
        w.normalize();
        weightsRepo.save(w);
    }

    //Khi user bỏ follow hoặc không xem bài follow
    public void adjustAfterUnfollowOrIgnore(Long userId) {
        UserFeedWeights w = weightsRepo.findById(userId).orElseThrow();
        w.setWFollowing(w.getWFollowing() - 0.03);
        w.normalize();
        weightsRepo.save(w);
    }

    //Khi user chỉ xem bài mới (new / recent)
    public void adjustForNewPostsOnly(Long userId) {
        UserFeedWeights w = weightsRepo.findById(userId).orElseThrow();
        w.setWRecentInteraction(w.getWRecentInteraction() + 0.02);
        w.setWTrending(w.getWTrending() - 0.01);
        w.normalize();
        weightsRepo.save(w);
    }

}
