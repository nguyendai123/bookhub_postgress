package com.bookhup.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "likes")
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id")
    private Long likeId;

    @ManyToOne
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    @ManyToOne
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JoinColumn(name="post_id", nullable=true)
    private Post post;

    @Column(name = "target_type", length = 20)
    private String targetType; // POST, COMMENT, REVIEW

    @Column(name = "target_id")
    private Long targetId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
