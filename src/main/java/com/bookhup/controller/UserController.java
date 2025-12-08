package com.bookhup.controller;

import com.bookhup.model.User;
import com.bookhup.dto.request.ProfileUpdateRequest;
import com.bookhup.service.auth.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PreAuthorize("hasAuthority('USER_UPDATE')")
    @PutMapping("/profile/update")
    public ResponseEntity<?> updateProfile(
            @RequestBody ProfileUpdateRequest request,
            @RequestHeader("Authorization") String token) {

        return ResponseEntity.ok(userService.updateProfile(request, token));
    }

    @PreAuthorize("hasAuthority('USER_DELETE')")
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<?> deleteUser(
            @PathVariable Long userId,
            @RequestHeader("Authorization") String token) {

        userService.deleteUser(userId, token);
        return ResponseEntity.ok("User deleted successfully");
    }

    @PostMapping("/avatar")
    public ResponseEntity<Map<String, String>> uploadAvatar(
            @RequestAttribute("currentUser") User user,
            @RequestParam("file") MultipartFile file
    ) {
        String avatarUrl = userService.uploadUserAvatar(user, file);

        Map<String, String> response = new HashMap<>();
        response.put("avatar_url", avatarUrl);

        return ResponseEntity.ok(response);
    }
}
