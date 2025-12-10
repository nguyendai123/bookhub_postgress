package com.bookhup.dto.response.post;

import java.time.LocalDateTime;
import java.util.List;

public interface PostFeedProjection {

    Long getPostId();
    Long getBookId();
    String getContent();
    String getImageUrl();
    List<String> getHashtags();
    LocalDateTime getUpdatedAt();

    Integer getLikesCount();
    Integer getCommentsCount();
    Integer getSharesCount();
    Integer getViews();

    Long getUserId();
    String getUserName();
    String getUserAvatar();

    Integer getIsLiked(); // quan trọng!
    String getPostTitle();
    String getTotalPages();
    String getReadingStatus();
    String getCurrentPage();
    String getPercentDone();

}
