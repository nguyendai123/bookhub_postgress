package com.bookhup.controller.generate_data;

import com.bookhup.controller.dto.GenerateUsersRequest;
import com.bookhup.controller.service.DevUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}

