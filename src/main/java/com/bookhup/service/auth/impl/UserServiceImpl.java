package com.bookhup.service.auth.impl;

import com.bookhup.exception.AppException;
import com.bookhup.exception.ErrorCode;
import com.bookhup.exception.ResourceNotFoundException;
import com.bookhup.jwts.JwtProvider;
import com.bookhup.model.User;
import com.bookhup.model.UserStatus;
import com.bookhup.repository.UserRepository;
import com.bookhup.dto.request.ProfileUpdateRequest;
import com.bookhup.service.auth.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.SecureRandom;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    private static final String RANDOM_CHAR_POOL =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    public User createUser(User user) {
        user.setCreatedAt(java.time.LocalDateTime.now());
        return userRepository.save(user);
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }

    @Override
    public Boolean checkUsernameOrEmailExisted(String username, String email) {
        return userRepository.existsByUsernameOrEmail(username, email);
    }

    @Override
    public String generateRandomPassword() {
        int length = 20;
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = RANDOM.nextInt(RANDOM_CHAR_POOL.length());
            sb.append(RANDOM_CHAR_POOL.charAt(index));
        }
        return sb.toString();
    }

    @Override
    public void changePassword(String email, String newPassword) {
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found: " + email));

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void deleteUser(Long userId, String token) {

        Long currentUserId = jwtProvider.extractUserId(token);
        Set<String> roles = jwtProvider.extractRoles(token); // ADMIN, USER...

        boolean isAdmin = roles.contains("ADMIN");

        if (!isAdmin && !currentUserId.equals(userId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        user.setStatus(UserStatus.DELETED);  // soft delete
        userRepository.save(user);
    }


    public User updateProfile(ProfileUpdateRequest request, String token) {

        Long userId = jwtProvider.extractUserId(token);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        user.setAvatarUrl(request.getAvatarUrl());
        user.setBio(request.getBio());
        user.setFavoriteGenres(request.getFavoriteGenres());
        user.setReadingPattern(request.getReadingPattern());
        user.setPreferredLanguage(request.getPreferredLanguage());
        user.setAvgReadTimePerDay(request.getAvgReadTimePerDay());
        user.setSocialLinks(request.getSocialLinks());

        userRepository.save(user);

        return user;
    }

    private static final String AVATAR_FOLDER = "src/main/resources/static/avatars/";

    public String uploadUserAvatar(User user, MultipartFile file) {

        try {
            // Tạo tên file: userId + đuôi
            String extension = Objects.requireNonNull(file.getOriginalFilename())
                    .substring(file.getOriginalFilename().lastIndexOf("."));

            String fileName = "user_" + user.getUserId() + extension;

            // Path lưu file vào static
            Path filePath = Paths.get(AVATAR_FOLDER + fileName);

            // Tạo thư mục nếu chưa có
            Files.createDirectories(filePath.getParent());

            // Ghi file
            Files.write(filePath, file.getBytes(), StandardOpenOption.CREATE);

            // URL để FE hiển thị
            String avatarUrl = "/avatars/" + fileName;

            user.setAvatarUrl(avatarUrl);
            userRepository.save(user);

            return avatarUrl;

        } catch (IOException e) {
            throw new RuntimeException("Upload avatar failed", e);
        }
    }
}
