package com.bookhup.controller;

import com.bookhup.dto.response.post.PostFeedDto;
import com.bookhup.dto.response.user.PostOfUserResponse;
import com.bookhup.dto.response.user.UserProfileResponse;
import com.bookhup.model.User;
import com.bookhup.dto.request.user.ProfileUpdateRequest;
import com.bookhup.service.PostService;
import com.bookhup.service.auth.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private final PostService postService;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}/profile")
    public ResponseEntity<UserProfileResponse> getUserProfile(
            @PathVariable Long userId,
            @RequestAttribute("currentUser") User user
    ) {
        return ResponseEntity.ok(
                userService.getUserProfile(userId, user)
        );
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

    @GetMapping("/{userId}/posts")
    public ResponseEntity<Page<PostFeedDto>> getUserPosts(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestAttribute("currentUser") User currentUser
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(
                postService.getUserPosts(userId, pageable)
        );
    }
}
