package com.thanhson.bookhup.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CommentID")
    private Long commentID;

    @Column(name = "Content")
    private String content;

    @Column(name = "LikeCount")
    private int likeCount;

    @Column(name = "CreateAt")
    private LocalDateTime createAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userID")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PostID")
    private Post post;

}
