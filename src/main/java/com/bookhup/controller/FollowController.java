package com.bookhup.controller;

import com.bookhup.dto.response.follow.FollowUserDTO;
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

    // ⭐ Những người đang theo dõi user này
    @GetMapping("/{userId}/followers")
    public List<FollowUserDTO> getFollowers(@PathVariable Long userId) {
        return followService.getFollowers(userId);
    }

    // ⭐ Những người user này đang theo dõi
    @GetMapping("/{userId}/following")
    public List<FollowUserDTO> getFollowing(@PathVariable Long userId) {
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
