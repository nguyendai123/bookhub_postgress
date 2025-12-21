package com.bookhup.dto.response.user;


import com.bookhup.dto.response.comment.CommentResponse;
import com.bookhup.dto.response.comment.CommentWithUserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostOfUserResponse {

    private Long postId;

    private Long userId;
    private String username;
    private String userAvatar;

    private String content;
    private String imageUrl;

    private Long bookId;
    private String bookTitle;

    private Integer likesCount;
    private Integer commentsCount;
    private Integer sharesCount;

    private boolean likedByCurrentUser;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private List<CommentWithUserDTO> comments;
}
