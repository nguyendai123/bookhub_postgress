package com.bookhup.dto.response.comment;

import com.bookhup.model.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponse {

    private Long commentId;
    private Long userId;
    private String username;
    private Long parentId;

    private String content;
    private String translatedText;

    private Integer likesCount;

    private LocalDateTime createdAt;

    // Nếu comment thuộc Post hoặc Review
    private Long postId;
    private Long reviewId;

    public static CommentResponse from(Comment c) {
        return CommentResponse.builder()
                .commentId(c.getCommentId())
                .userId(c.getUser().getUserId())
                .username(c.getUser().getUsername())
                .parentId(c.getParentId())
                .content(c.getContent())
                .translatedText(c.getTranslatedText())
                .likesCount(c.getLikesCount())
                .createdAt(c.getCreatedAt())
                .postId(c.getPost() != null ? c.getPost().getPostId() : null)
                .reviewId(c.getReview() != null ? c.getReview().getReviewId() : null)
                .build();
    }
}


