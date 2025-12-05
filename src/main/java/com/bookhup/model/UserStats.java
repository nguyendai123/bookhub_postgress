package com.bookhup.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_stats")
public class UserStats {

    @Id
    @Column(name = "user_id")
    private Long userId; // 1-1 mapping to users

    @MapsId
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "total_books_read")
    @Builder.Default
    private Integer totalBooksRead = 0;

    @Column(name = "total_reviews")
    @Builder.Default
    private Integer totalReviews = 0;

    @Column(name = "total_likes_received")
    @Builder.Default
    private Integer totalLikesReceived = 0;

    @Column(name = "total_followers")
    @Builder.Default
    private Integer totalFollowers = 0;

    @Column(name = "rank_position")
    @Builder.Default
    private Integer rankPosition = 0;

    @Column(name = "daily_limit", nullable = false)
    private Integer dailyLimit;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
