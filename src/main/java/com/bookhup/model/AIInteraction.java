package com.bookhup.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ai_interaction")
public class AIInteraction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "interaction_id")
    private Long interactionId;

    @ManyToOne
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JoinColumn(name = "book_id")
    private Book book;

    @Lob
    private String question;

    @Lob
    private String answer;

    @Column(name = "context_book_id")
    private Long contextBookId;

    @Column(name = "confidence_score")
    private Float confidenceScore;

    @Column(name = "model_version")
    private String modelVersion;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
