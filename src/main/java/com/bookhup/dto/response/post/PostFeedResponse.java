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
    private LocalDateTime updatedAt;

    private Integer likesCount;
    private Integer commentsCount;
    private Integer sharesCount;
    private Integer views;

    private Integer isLiked;

    private Long userId;
    private String userName;
    private String userAvatar;
    private String readPage;
    private String totalPage;

    public static PostFeedResponse fromProjection(PostFeedProjection p){
        return PostFeedResponse.builder()
                .postId(p.getPostId())
                .bookId(p.getBookId())
                .content(p.getContent())
                .imageUrl(p.getImageUrl())
                .hashtags(p.getHashtags())
                .updatedAt(p.getUpdatedAt())
                .likesCount(p.getLikesCount())
                .commentsCount(p.getCommentsCount())
                .sharesCount(p.getSharesCount())
                .views(p.getViews())
                .isLiked(p.getIsLiked())
                .userId(p.getUserId())
                .userName(p.getUserName())
                .userAvatar(p.getUserAvatar())
                .readPage(p.getCurrentPage())
                .totalPage(p.getTotalPages())
                .build();
    }
}

