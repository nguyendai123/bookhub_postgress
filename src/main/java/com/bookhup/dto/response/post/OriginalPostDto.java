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
public class OriginalPostDto {

    private Long postId;
    private Long userId;
    private Long bookId;
    private String userName;
    private String userAvatar;

    private String content;
    private String imageUrl;
    private List<String> hashtags;

    private Integer likesCount;
    private Integer commentsCount;
    private Integer sharesCount;
    private Long shareOf;
    private Integer views;
    private LocalDateTime updatedAt;

    private Integer isLiked;
    private String totalPages;
    private String readingStatus;
    private String currentPage;
    private String percentDone;
}

