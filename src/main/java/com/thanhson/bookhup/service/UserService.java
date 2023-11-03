package com.thanhson.bookhup.service;

import com.thanhson.bookhup.exception.ResourceNotFoundException;
import com.thanhson.bookhup.model.User;
import com.thanhson.bookhup.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User getUserById(Long userID) {
        return userRepository.getById(userID);
    }

    public Boolean checkUsernameOrEmailExisted(String username, String email) {
        return userRepository.existsByUsernameOrEmail(username, email);
    }

    public String generateRandomPassword() {
        int n = 20;
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {
            int index = (int) (AlphaNumericString.length()
                    * Math.random());

            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }

    public void changePassword(String usernameOrEmail, String newPassword) {
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User can not be found with username or email:" + usernameOrEmail));
        user.setPassword(passwordEncoder.encode(newPassword));

        userRepository.save(user);
    }

    public User updateUser(Long id, User userInfoRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User can not find"));

        user.setUsername(userInfoRequest.getUsername());
        user.setEmail(userInfoRequest.getEmail());
        user.setBiography(userInfoRequest.getBiography());
        user.setFavoriteGenres(user.getFavoriteGenres());
        if (userInfoRequest.getAvatar() != null) {
            user.setAvatar(userInfoRequest.getAvatar());
        }
        if (userInfoRequest.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(userInfoRequest.getPassword()));
        }
        userRepository.save(user);

        return new User(user.getUserID(), user.getUsername(), user.getEmail(),
                user.getAvatar(), user.getBiography(), user.getFavoriteGenres());
    }

    public User findById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
