package com.bookhup.controller;

import com.bookhup.dto.request.hashtag.PostHashtagRequest;
import com.bookhup.model.User;
import com.bookhup.service.HashtagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hashtags")
@RequiredArgsConstructor
public class HashtagController {

    private final HashtagService hashtagService;

    @PostMapping("/add")
    public ResponseEntity<?> addHashtags(
            @RequestBody PostHashtagRequest req,
            @RequestAttribute("currentUser") User user) {

        return ResponseEntity.ok(hashtagService.addHashtags(req, user));
    }
}

