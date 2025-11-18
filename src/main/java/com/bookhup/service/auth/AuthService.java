package com.bookhup.service.auth;

import com.bookhup.request.auth.LoginRequest;
import com.bookhup.request.auth.RegisterRequest;
import com.bookhup.request.auth.ResetPasswordRequest;
import com.bookhup.response.MessageResponse;
import com.bookhup.response.auth.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    String logout();

    MessageResponse resetPassword(ResetPasswordRequest request);
}

