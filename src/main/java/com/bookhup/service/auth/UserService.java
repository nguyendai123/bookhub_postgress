package com.bookhup.service.auth;

import com.bookhup.model.User;

import java.util.List;

public interface UserService {
    User createUser(User user);

    User getUserById(Long userId);

    Boolean checkUsernameOrEmailExisted(String username, String email);

    String generateRandomPassword();

    void changePassword(String email, String newPassword);

    User updateUser(Long id, User request);

    List<User> getAllUsers();

    void deleteUser(Long id);
}


