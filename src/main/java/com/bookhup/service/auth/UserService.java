package com.bookhup.service.auth;

import com.bookhup.model.User;
import com.bookhup.request.ProfileUpdateRequest;

import java.util.List;

public interface UserService {
    User createUser(User user);

    User getUserById(Long userId);

    Boolean checkUsernameOrEmailExisted(String username, String email);

    String generateRandomPassword();

    void changePassword(String email, String newPassword);

    List<User> getAllUsers();

    void deleteUser(Long userId, String token);

    User updateProfile(ProfileUpdateRequest request, String token);
}


