package com.bookhup.controller.generate_data.controller;
import com.bookhup.controller.generate_data.dto.GenerateAuthorsRequest;
import com.bookhup.controller.generate_data.dto.GenerateUsersRequest;
import com.bookhup.controller.generate_data.service.DevAuthorService;
import com.bookhup.controller.generate_data.service.DevUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/dev")
@RequiredArgsConstructor
public class DevUserController {

    private final DevUserService devUserService;

    @PostMapping("/generate-users")
    public ResponseEntity<?> generateUsers(@RequestBody GenerateUsersRequest request) {
        devUserService.generateUsers(request.getCount(), request.getPassword());
        return ResponseEntity.ok("Generated " + request.getCount() + " users successfully");
    }
    private final DevAuthorService devAuthorService;

    @PostMapping("/generate-authors")
    public ResponseEntity<?> generateAuthors(@RequestBody GenerateAuthorsRequest request) {
        devAuthorService.generateAuthors(request.getCount());
        return ResponseEntity.ok("Generated " + request.getCount() + " authors successfully");
    }
}

