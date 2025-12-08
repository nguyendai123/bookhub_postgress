package com.bookhup.dto.response.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class LoginResponse {
    private Long userId;
    private String username;
    private String avatar;
    private Set<String> roles;
    private String message;
    private String token;
}

