package com.bookhup.controller;

import com.bookhup.dto.request.like.LikeRequest;
import com.bookhup.model.User;
import com.bookhup.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/like")
    public ResponseEntity<?> like(@RequestBody LikeRequest req,
                                  @RequestAttribute("currentUser") User user) {

        return ResponseEntity.ok(likeService.toggleLike(req, user));
    }

    @PostMapping("/unlike")
    public ResponseEntity<String> unlike(
            @RequestBody LikeRequest req,
            @RequestAttribute("currentUser") User user
    ) {
        String message = likeService.toggleUnlike(req, user);
        return ResponseEntity.ok(message);
    }

}
