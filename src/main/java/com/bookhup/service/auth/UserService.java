package com.bookhup.service.auth;

import com.bookhup.dto.response.user.UserProfileResponse;
import com.bookhup.model.User;
import com.bookhup.dto.request.user.ProfileUpdateRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {
    UserProfileResponse getUserProfile(Long userId, User user);

    User createUser(User user);

    User getUserById(Long userId);

    Boolean checkUsernameOrEmailExisted(String username, String email);

    String generateRandomPassword();

    void changePassword(String email, String newPassword);

    List<User> getAllUsers();

    void deleteUser(Long userId, String token);

    User updateProfile(ProfileUpdateRequest request, String token);

    String uploadUserAvatar(User user, MultipartFile file);
}


