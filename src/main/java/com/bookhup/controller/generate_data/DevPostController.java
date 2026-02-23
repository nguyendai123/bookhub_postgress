package com.bookhup.controller.generate_data;

import com.bookhup.controller.dto.BulkPostGenerateRequest;
import com.bookhup.controller.service.DevAuthorService;
import com.bookhup.controller.service.DevPostService;
import com.bookhup.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dev")
@RequiredArgsConstructor
public class DevPostController {

    private final DevPostService devPostService;

    @PostMapping("/generate")
    public ResponseEntity<List<Post>> generatePosts(@RequestBody BulkPostGenerateRequest request) throws Exception {
        return ResponseEntity.ok(devPostService.generateFakePosts(request.getTotalPosts()));
    }
}

