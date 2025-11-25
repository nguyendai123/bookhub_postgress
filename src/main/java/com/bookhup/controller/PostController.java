package com.bookhup.controller;

import com.bookhup.model.Post;
import com.bookhup.model.User;
import com.bookhup.dto.request.post.PostRequest;
import com.bookhup.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PreAuthorize("hasAuthority('POST_CREATE')")
    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody PostRequest request, @RequestAttribute("currentUser") User user) {
        return ResponseEntity.ok(postService.createPost(request, user));
    }

    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    @GetMapping("/{postId}")
    public Post getPost(@PathVariable Long postId) {
        return postService.getPost(postId);
    }

    @PreAuthorize("hasAuthority('POST_UPDATE')")
    @PutMapping("/{postId}")
    public ResponseEntity<Post> updatePost(@PathVariable Long postId,
                                           @RequestBody Post post,
                                           @RequestAttribute("currentUser") User user) {
        return ResponseEntity.ok(postService.updatePost(postId, post, user));
    }

    @PreAuthorize("hasAuthority('POST_DELETE')")
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId,
                                           @RequestAttribute("currentUser") User user) {
        postService.deletePost(postId, user);
        return ResponseEntity.noContent().build();
    }
}
