package com.bookhup.service.auth.impl;

import com.bookhup.dto.request.auth.ChangePasswordRequest;
import com.bookhup.dto.request.auth.LoginRequest;
import com.bookhup.dto.request.auth.RegisterRequest;
import com.bookhup.dto.request.auth.ResetPasswordRequest;
import com.bookhup.dto.response.MessageResponse;
import com.bookhup.dto.response.auth.AuthResponse;
import com.bookhup.jwts.JwtProvider;
import com.bookhup.model.*;
import com.bookhup.repository.PermissionRepository;
import com.bookhup.repository.RoleRepository;
import com.bookhup.repository.UserFeedWeightsRepository;
import com.bookhup.repository.UserRepository;
import com.bookhup.service.EmailService;
import com.bookhup.service.auth.AuthService;
import com.bookhup.service.auth.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static com.bookhup.model.UserStatus.ACTIVE;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserFeedWeightsRepository weightsRepo;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final UserService userService;
    private final JwtProvider jwtProvider;

    @Override
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByUsernameOrEmail(request.getUsername(), request.getEmail())) {
            throw new RuntimeException("Username or Email already in use");
        }

        Role roleUser = roleRepository.findByRoleName(RoleType.USER)
                .orElseThrow(() -> new RuntimeException("Default USER role not found"));

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .createdAt(LocalDateTime.now())
                .isAdmin(false)
                .status(ACTIVE)
                .roles(new HashSet<>())
                .build();

        user.getRoles().add(roleUser);
        userRepository.save(user);

        createDefaultWeights(user.getUserId());
        return new AuthResponse("Register success", null);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsernameActive(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid Username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid email or password");
        }

        // Lấy roles từ repo riêng
        Set<String> roles = roleRepository.findRolesByUserId(user.getUserId())
                .stream()
                .map(r -> r.getRoleName().name())
                .collect(Collectors.toSet());

        // Lấy permissions từ repo riêng
        Set<String> permissions = permissionRepository.findPermissionsByUserId(user.getUserId())
                .stream()
                .map(Permission::getPermissionName)
                .collect(Collectors.toSet());

        String jwt = jwtProvider.generateToken(user.getUserId(), roles, permissions);

        return new AuthResponse("Login success", jwt);
    }

    @Override
    public String logout() {
        return "Logout success (JWT handled on client side)";
    }

    @Override
    public MessageResponse resetPassword(ResetPasswordRequest request) {

        User user = userRepository.findByUsernameOrEmail(request.getUsername(), request.getEmail())
                .orElseThrow(() -> new RuntimeException("Username or Email not found"));

        String email = user.getEmail();
        String newPassword = userService.generateRandomPassword();

        userService.changePassword(email, newPassword);
        // Gửi mail bất đồng bộ
        emailService.sendMailResetPassword(newPassword, email);

        MessageResponse response = new MessageResponse();
        response.setMessage("Reset password instruction sent to email. Email sending runs in background.");
        return response;
    }

    @Override
    public Object changePassword(User currentUser, ChangePasswordRequest request) {

        // Validate current password
        if (!passwordEncoder.matches(request.getOldPassword(), currentUser.getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Mật khẩu hiện tại không đúng!");
        }

        // Set new password
        currentUser.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(currentUser);

        return ResponseEntity.ok("Mật khẩu tài khoản của bạn đã được thay đổi.");

    }

    public void createDefaultWeights(Long userId) {
        UserFeedWeights w = UserFeedWeights.builder()
                .userId(userId)
                .wRecentInteraction(0.5)
                .wFollowing(0.3)
                .wTrending(0.2)
                .build();
        weightsRepo.save(w);
    }


}

