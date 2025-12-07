package com.bookhup.listener;

import com.bookhup.event.UserRegisteredEvent;
import com.bookhup.model.User;
import com.bookhup.model.UserStats;
import com.bookhup.repository.UserStatsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class UserStatsCreator {

    private final UserStatsRepository statsRepo;

    // Giới hạn thông báo mỗi ngày (default)
    private final int DEFAULT_DAILY_LIMIT_NOTI = 5;

    @EventListener
    public void handle(UserRegisteredEvent event) {

        User user = event.getUser();

        UserStats stats = UserStats.builder()
                .user(user)                    // Quan hệ @OneToOne
                .userId(user.getUserId())          // @MapsId
                .dailyLimitNoti(DEFAULT_DAILY_LIMIT_NOTI)
                .totalBooksRead(0)
                .totalReviews(0)
                .totalLikesReceived(0)
                .totalFollowers(0)
                .rankPosition(0)
                .updatedAt(LocalDateTime.now())
                .build();

        statsRepo.save(stats);
    }
}