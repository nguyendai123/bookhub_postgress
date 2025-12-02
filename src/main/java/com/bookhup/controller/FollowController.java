package com.bookhup.controller;

import com.bookhup.model.Follow;
import com.bookhup.model.User;
import com.bookhup.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/follow")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @PostMapping("/{targetUserId}")
    public String follow(@RequestAttribute("currentUser") User user,
                         @PathVariable Long targetUserId) {
        followService.follow(user, targetUserId);
        return "Follow thành công.";
    }

    @DeleteMapping("/{targetUserId}")
    public String unfollow(@RequestAttribute("currentUser") User user,
                           @PathVariable Long targetUserId) {
        followService.unfollow(user, targetUserId);
        return "Unfollow thành công.";
    }

    // ⭐ Lấy danh sách cac follower ( nhung nguoi dang theo doi userId)
    @GetMapping("/{userId}/followers")
    public List<Follow> getFollowers(@PathVariable Long userId) {
        return followService.getFollowers(userId);
    }

    // ⭐ Lấy danh sách cac following (userId dang theo doi nhung ai)
    @GetMapping("/{userId}/following")
    public List<Follow> getFollowing(@PathVariable Long userId) {
        return followService.getFollowing(userId);
    }

    // ⭐ Kiểm tra follow
    @GetMapping("/check")
    public boolean isFollowing(
            @RequestParam Long followerId,
            @RequestParam Long followingId) {
        return followService.isFollowing(followerId, followingId);
    }
}
