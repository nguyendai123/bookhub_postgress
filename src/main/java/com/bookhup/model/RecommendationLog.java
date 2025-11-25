package com.bookhup.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "recommendation_log")
public class RecommendationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rec_id")
    private Long recId;

    @ManyToOne
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "recommended_book_id")
    private Long recommendedBookId;

    @Column(length = 50)
    private String algorithm;

    @Column(name = "confidence_score")
    private Float confidenceScore;

    @Column(length = 10)
    private String feedback; // LIKE, DISLIKE, IGNORE

    @Column(name = "timestamp")
    private LocalDateTime timestamp;
}
