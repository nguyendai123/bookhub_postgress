package com.bookhup.service.auth.impl;

import com.bookhup.exception.AppException;
import com.bookhup.exception.ErrorCode;
import com.bookhup.exception.ResourceNotFoundException;
import com.bookhup.jwts.JwtProvider;
import com.bookhup.model.User;
import com.bookhup.repository.UserRepository;
import com.bookhup.request.ProfileUpdateRequest;
import com.bookhup.service.auth.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
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

        user.setStatus("DELETED");  // soft delete
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
}
