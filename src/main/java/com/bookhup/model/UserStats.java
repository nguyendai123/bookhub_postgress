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
    private Integer totalBooksRead;

    @Column(name = "total_reviews")
    private Integer totalReviews;

    @Column(name = "total_likes_received")
    private Integer totalLikesReceived;

    @Column(name = "total_followers")
    private Integer totalFollowers;

    @Column(name = "rank_position")
    private Integer rankPosition;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
