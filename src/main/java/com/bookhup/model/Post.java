package com.bookhup.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long postId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Lob
    private String content;

    @Lob
    @Column(name = "translated_text")
    private String translatedText;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @Lob
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private List<String> hashtags; // JSON array of tags

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = true)
    private Book book;

    @Column(name = "share_of")
    private Long shareOf;

    private Integer likesCount;
    private Integer commentsCount;
    private Integer sharesCount;
    private Integer views;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Comments on this Post
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Comment> comments = new HashSet<>();

    // Likes on this Post
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Like> likes = new HashSet<>();

    // Shares of this Post
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Share> shares = new HashSet<>();
}
