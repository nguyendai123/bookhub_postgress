package com.bookhup.controller;

import com.bookhup.model.Comment;
import com.bookhup.model.User;
import com.bookhup.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PreAuthorize("hasAuthority('COMMENT_CREATE')")
    @PostMapping
    public ResponseEntity<Comment> createComment(@RequestBody Comment comment,
                                                 @RequestParam Long postId,
                                                 @RequestParam(required = false) Long parentId,
                                                 @RequestAttribute("currentUser") User user) {
        return ResponseEntity.ok(commentService.createComment(comment, user, postId, parentId));
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<Comment>> getCommentsByPost(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.getCommentsByPost(postId));
    }

    @PreAuthorize("hasAuthority('COMMENT_UPDATE')")
    @PutMapping("/{commentId}")
    public ResponseEntity<Comment> updateComment(@PathVariable Long commentId,
                                                 @RequestBody Comment comment,
                                                 @RequestAttribute("currentUser") User user) {
        return ResponseEntity.ok(commentService.updateComment(commentId, comment, user));
    }

    @PreAuthorize("hasAuthority('COMMENT_DELETE')")
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId,
                                              @RequestAttribute("currentUser") User user) {
        commentService.deleteComment(commentId, user);
        return ResponseEntity.noContent().build();
    }
}
