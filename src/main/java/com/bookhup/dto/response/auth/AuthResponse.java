package com.bookhup.dto.response.auth;

import com.bookhup.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
public class AuthResponse {
    private Long userId;
    private String username;
    private Set<String> roles;
    private String message;
    private String token;
}

