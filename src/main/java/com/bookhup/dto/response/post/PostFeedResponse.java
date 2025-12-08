package com.bookhup.dto.response.post;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PostFeedResponse {

    private Long postId;
    private Long bookId;
    private String content;
    private String imageUrl;
    private List<String> hashtags;
    private LocalDateTime createdAt;

    private Integer likesCount;
    private Integer commentsCount;
    private Integer sharesCount;
    private Integer views;

    private boolean isLiked;

    private Long userId;
    private String userName;
    private String userAvatar;

    public static PostFeedResponse fromProjection(PostFeedProjection p){
        return PostFeedResponse.builder()
                .postId(p.getPostId())
                .bookId(p.getBookId())
                .content(p.getContent())
                .imageUrl(p.getImageUrl())
                .hashtags(p.getHashtags())
                .createdAt(p.getCreatedAt())
                .likesCount(p.getLikesCount())
                .commentsCount(p.getCommentsCount())
                .sharesCount(p.getSharesCount())
                .views(p.getViews())
                .isLiked(p.getIsLiked())
                .userId(p.getUserId())
                .userName(p.getUserName())
                .userAvatar(p.getUserAvatar())
                .build();
    }
}

