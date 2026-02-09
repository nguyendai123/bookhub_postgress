package com.bookhup.controller.generate_data;

import com.bookhup.controller.dto.GenerateAuthorsRequest;
import com.bookhup.controller.service.DevAuthorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dev")
@RequiredArgsConstructor
public class DevAuthorController {

    private final DevAuthorService devAuthorService;

    @PostMapping("/generate-authors")
    public ResponseEntity<?> generateAuthors(@RequestBody GenerateAuthorsRequest request) {
        devAuthorService.generateAuthors(request.getCount());
        return ResponseEntity.ok("Generated " + request.getCount() + " authors successfully");
    }
}
