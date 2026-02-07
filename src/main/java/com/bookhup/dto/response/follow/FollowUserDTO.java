package com.bookhup.dto.response.follow;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FollowUserDTO {
    private Long userId;
    private String username;
    private String avatarUrl;
    private String bio;
}

