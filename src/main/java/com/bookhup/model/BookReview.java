package com.bookhup.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "book_review")
public class BookReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    private Integer rating;

    @Lob
    private String comment;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @Column(length = 10)
    private String lang;

    @Column(name = "ai_sentiment_score")
    private Float aiSentimentScore;

    @Lob
    @Column(name = "translated_text")
    private String translatedText;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Comments on this BookReview
    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Comment> comments = new HashSet<>();
}
