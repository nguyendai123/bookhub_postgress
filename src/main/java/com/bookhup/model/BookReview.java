package com.bookhup.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

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
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JoinColumn(name = "book_id")
    private Book book;

    @Builder.Default
    private Integer rating = 0;

    @Lob
    private String comment;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @Column(length = 10)
    private String lang;

    @Column(name = "ai_sentiment_score")
    @Builder.Default
    private Float aiSentimentScore = 0.0f;

    @Lob
    @Column(name = "translated_text")
    private String translatedText;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Comments on this BookReview
    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Comment> comments = new HashSet<>();

    @OneToMany(mappedBy = "bookReview", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Like> likes = new HashSet<>();
}
