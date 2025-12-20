package com.bookhup.dto.response.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserProfileResponse {
    private Long userId;
    private String username;
    private String avatar;
    private String bio;
    private long followersCount;
    private long followingCount;
    private boolean isFollowing;
}