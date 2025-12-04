package com.bookhup.controller;//package com.bookhup.controller;

import com.bookhup.dto.request.auth.ChangePasswordRequest;
import com.bookhup.dto.request.auth.LoginRequest;
import com.bookhup.dto.request.auth.RegisterRequest;
import com.bookhup.dto.request.auth.ResetPasswordRequest;
import com.bookhup.dto.response.MessageResponse;
import com.bookhup.model.User;
import com.bookhup.service.auth.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok(authService.logout());
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        MessageResponse response = authService.resetPassword(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestBody ChangePasswordRequest request,
            @RequestAttribute("currentUser") User currentUser // giả sử bạn lấy user từ JWT hoặc session
    ) {
        return ResponseEntity.ok(authService.changePassword(currentUser, request));
    }
}

