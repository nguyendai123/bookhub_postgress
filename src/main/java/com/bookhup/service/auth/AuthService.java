package com.bookhup.service.auth;

import com.bookhup.dto.request.auth.ChangePasswordRequest;
import com.bookhup.dto.request.auth.LoginRequest;
import com.bookhup.dto.request.auth.RegisterRequest;
import com.bookhup.dto.request.auth.ResetPasswordRequest;
import com.bookhup.dto.response.MessageResponse;
import com.bookhup.dto.response.auth.AuthResponse;
import com.bookhup.model.User;

public interface AuthService {
    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    String logout();

    MessageResponse resetPassword(ResetPasswordRequest request);

    Object changePassword(User currentUser, ChangePasswordRequest request);
}

