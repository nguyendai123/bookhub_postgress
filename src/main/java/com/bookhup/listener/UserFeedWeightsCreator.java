package com.bookhup.listener;

import com.bookhup.event.UserRegisteredEvent;
import com.bookhup.model.User;
import com.bookhup.model.UserFeedWeights;
import com.bookhup.repository.UserFeedWeightsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserFeedWeightsCreator {

    private final UserFeedWeightsRepository weightsRepo;

    @EventListener
    public void handle(UserRegisteredEvent event) {
        User user = event.getUser();
        UserFeedWeights w = UserFeedWeights.builder()
                .userId(user.getUserId())
                .wRecentInteraction(0.5)
                .wFollowing(0.3)
                .wTrending(0.2)
                .build();
        weightsRepo.save(w);
    }
}