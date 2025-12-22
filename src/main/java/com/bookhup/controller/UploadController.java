package com.bookhup.controller;

import com.bookhup.model.UploadType;
import com.bookhup.model.User;
import com.bookhup.service.upload.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/uploads")
@RequiredArgsConstructor
public class UploadController {

    private final FileUploadService uploadService;

    @PostMapping("/{type}")
    public ResponseEntity<Map<String, String>> upload(
            @PathVariable UploadType type,
            @RequestParam("file") MultipartFile file,
            @RequestAttribute("currentUser") User user
    ) {
        return ResponseEntity.ok(Map.of("url",uploadService.uploadFile(file, type, user.getUserId())));

    }
}
