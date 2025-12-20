package com.bookhup.dto.response.comment;

import java.time.LocalDateTime;

public interface CommentWithUserDTO {
    Long getCommentId();
    String getContent();
    Long getParentId();
    String getTranslatedText();
    LocalDateTime getCreatedAt();

    UserInfo getUser();

    interface UserInfo {
        Long getUserId();
        String getUsername();
        String getAvatarUrl();
    }
}

