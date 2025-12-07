package com.bookhup.dto.response.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private Long userId;
    private String username;
    private String message;
    private String token;
}

