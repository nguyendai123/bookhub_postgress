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

    public void normalize() {
        double total = wRecentInteraction + wFollowing + wTrending;

        wRecentInteraction /= total;
        wFollowing /= total;
        wTrending /= total;
    }
}

