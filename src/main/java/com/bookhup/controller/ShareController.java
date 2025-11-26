package com.bookhup.controller;

import com.bookhup.dto.request.share.ShareRequest;
import com.bookhup.model.User;
import com.bookhup.service.ShareService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/share")
@RequiredArgsConstructor
public class ShareController {

    private final ShareService shareService;

    @PostMapping
    public ResponseEntity<?> share(@RequestBody ShareRequest req,
                                   @RequestAttribute("currentUser") User user) {

        return ResponseEntity.ok(shareService.sharePost(req, user));
    }
}

