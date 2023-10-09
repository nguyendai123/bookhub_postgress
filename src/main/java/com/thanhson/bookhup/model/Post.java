package com.thanhson.bookhup.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Blob;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PostID")
    private Long postID;
    @Column(name = "Content")
    private String content;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createDate;
    @Column(name = "LikeCount")
    private int likeCount;
    @Column(name = "Rating")
    private double rating;
    @Column(name = "ImageData",columnDefinition = "TEXT")
    private String imageData;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userID")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bookID")
    private Book book;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private Set<Like> likes;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private Set<Comment> comments;

    public List<User> getLikedUsers() {
        List<User> likedUsers = new ArrayList<>();

        for (Like like : likes) {
            likedUsers.add(like.getUser());
        }

        return likedUsers;
    }
}
