package com.bookhup.controller;

import com.bookhup.dto.request.readingProgress.ReadingUpdateRequest;
import com.bookhup.dto.request.shelf.ReadingAddRequest;
import com.bookhup.model.User;
import com.bookhup.service.ReadingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reading")
@RequiredArgsConstructor
public class ReadingController {

    private final ReadingService service;

    // --- Add book to shelf ---
    @PostMapping("/add")
    public ResponseEntity<?> addBook(
            @RequestAttribute("currentUser") User user,
            @RequestBody ReadingAddRequest request) {

        return ResponseEntity.ok(service.addToShelf(user, request));
    }

    // --- Update reading progress ---
    @PostMapping("/update")
    public ResponseEntity<?> update(
            @RequestAttribute("currentUser") User user,
            @RequestBody ReadingUpdateRequest request) {

        return ResponseEntity.ok(service.updateProgress(user, request));
    }
}

