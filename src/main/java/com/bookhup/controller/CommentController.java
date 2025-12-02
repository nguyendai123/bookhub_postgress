package com.bookhup.controller;

import com.bookhup.dto.response.comment.CommentResponse;
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

    // ========================= CREATE COMMENT (POST) =========================
    @PreAuthorize("hasAuthority('COMMENT_CREATE')")
    @PostMapping("/post/{postId}")
    public ResponseEntity<Comment> commentPost(
            @PathVariable Long postId,
            @RequestBody Comment request,
            @RequestAttribute("currentUser") User currentUser
    ) {
        return ResponseEntity.ok(
                commentService.addCommentToPost(
                        postId,
                        currentUser,
                        request.getContent(),
                        request.getParentId()
                )
        );
    }

    // ========================= CREATE COMMENT (REVIEW) =========================
    @PreAuthorize("hasAuthority('COMMENT_CREATE')")
    @PostMapping("/review/{reviewId}")
    public ResponseEntity<Comment> commentReview(
            @PathVariable Long reviewId,
            @RequestBody Comment request,
            @RequestAttribute("currentUser") User currentUser
    ) {
        return ResponseEntity.ok(
                commentService.addCommentToReview(
                        reviewId,
                        currentUser,
                        request.getContent(),
                        request.getParentId()
                )
        );
    }

    // ========================= GET COMMENTS =========================
    @PreAuthorize("hasAuthority('COMMENT_READ')")
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<Comment>> getPostComments(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.getCommentsByPost(postId));
    }

    @PreAuthorize("hasAuthority('COMMENT_READ')")
    @GetMapping("/review/{reviewId}")
    public ResponseEntity<List<CommentResponse>> getReviewComments(@PathVariable Long reviewId) {
        return ResponseEntity.ok(commentService.getCommentsByReview(reviewId));
    }

    @PreAuthorize("hasAuthority('COMMENT_READ')")
    @GetMapping("/replies/{parentId}")
    public ResponseEntity<List<Comment>> getReplies(@PathVariable Long parentId) {
        return ResponseEntity.ok(commentService.getReplies(parentId));
    }

    // ========================= UPDATE COMMENT =========================
    @PreAuthorize("hasAuthority('COMMENT_UPDATE')")
    @PutMapping("/{commentId}")
    public ResponseEntity<Comment> updateComment(
            @PathVariable Long commentId,
            @RequestBody Comment request,
            @RequestAttribute("currentUser") User currentUser
    ) {
        return ResponseEntity.ok(
                commentService.updateComment(
                        commentId,
                        currentUser,
                        request.getContent()
                )
        );
    }

    // ========================= DELETE COMMENT =========================
    @PreAuthorize("hasAuthority('COMMENT_DELETE')")
    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> deleteComment(
            @PathVariable Long commentId,
            @RequestAttribute("currentUser") User currentUser
    ) {
        commentService.deleteComment(commentId, currentUser);
        return ResponseEntity.ok("Comment deleted");
    }
}
