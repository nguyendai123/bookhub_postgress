package com.bookhup.dto.response.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostFeedDto {

    private Long postId;
    private Long bookId;
    private String content;
    private String imageUrl;
    private List<String> hashtags;
    private LocalDateTime updatedAt;

    private Integer likesCount;
    private Integer commentsCount;
    private Integer sharesCount;
    private Long shareOf;
    private Integer views;

    private Long userId;
    private String userName;
    private String userAvatar;

    private Integer isLiked;
    private String totalPages;
    private String readingStatus;
    private String currentPage;
    private String percentDone;

    // Bài viết gốc (nếu là bài share)
    private OriginalPostDto originalPost;
}

