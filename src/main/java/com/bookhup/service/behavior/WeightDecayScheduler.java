package com.bookhup.service.behavior;

import com.bookhup.repository.UserFeedWeightsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WeightDecayScheduler {

    private final UserFeedWeightsRepository repo;

    @Scheduled(cron = "0 0 3 * * MON") // mỗi thứ 2 hàng tuần lúc 3h sáng
    public void decay() {
        repo.findAll().forEach(w -> {
            w.setWTrending(w.getWTrending() * 0.98);
            w.setWFollowing(w.getWFollowing() * 0.98);
            w.setWRecentInteraction(w.getWRecentInteraction() * 0.98);
            w.normalize();
            repo.save(w);
        });
    }
}

