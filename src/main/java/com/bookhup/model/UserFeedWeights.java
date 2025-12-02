package com.bookhup.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Entity
@Table(name = "user_feed_weights")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserFeedWeights {

    @Id
    private Long userId;

    @Column(name = "w_recent_interaction")
    @Builder.Default
    private double wRecentInteraction = 0.5;

    @Column(name = "w_following")
    @Builder.Default
    private double wFollowing = 0.3;

    @Column(name = "w_trending")
    @Builder.Default
    private double wTrending = 0.2;

    @Column(name = "last_update")
    private LocalDateTime lastUpdate = LocalDateTime.now();

    public UserFeedWeights(Long userId, double wRecentInteraction, double wFollowing, double wTrending) {
        this.userId = userId;
        this.wRecentInteraction = wRecentInteraction;
        this.wFollowing = wFollowing;
        this.wTrending = wTrending;
    }

    // Scale tổng về 1.0 và không cho âm
    public void normalize() {
        wRecentInteraction = Math.max(0, wRecentInteraction);
        wFollowing = Math.max(0, wFollowing);
        wTrending = Math.max(0, wTrending);

        double sum = wRecentInteraction + wFollowing + wTrending;
        if (sum == 0) {
            wRecentInteraction = 0.5;
            wFollowing = 0.3;
            wTrending = 0.2;
            return;
        }

        wRecentInteraction /= sum;
        wFollowing /= sum;
        wTrending /= sum;
    }
}

