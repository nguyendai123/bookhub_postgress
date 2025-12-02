package com.bookhup.controller;

import com.bookhup.dto.request.review.ReviewRequest;
import com.bookhup.dto.response.comment.CommentResponse;
import com.bookhup.dto.response.review.ReviewResponse;
import com.bookhup.model.User;
import com.bookhup.service.BookReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class BookReviewController {

    private final BookReviewService reviewService;

    // 1️⃣ Tạo review
    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(
            @AuthenticationPrincipal User user,
            @RequestBody ReviewRequest request) {
        return ResponseEntity.ok(reviewService.createReview(user, request));
    }

    // 2️⃣ Update review
    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> updateReview(
            @AuthenticationPrincipal User user,
            @PathVariable Long reviewId,
            @RequestBody ReviewRequest request) {
        return ResponseEntity.ok(reviewService.updateReview(user, reviewId, request));
    }

    // 3️⃣ Delete review
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @AuthenticationPrincipal User user,
            @PathVariable Long reviewId) {
        reviewService.deleteReview(user, reviewId);
        return ResponseEntity.ok().build();
    }

    // 4️⃣ Get review by id
    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> getReview(@PathVariable Long reviewId) {
        return ResponseEntity.ok(reviewService.getReview(reviewId));
    }

    // 5️⃣ Get reviews of book
    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsOfBook(
            @PathVariable Long bookId) {
        return ResponseEntity.ok(reviewService.getReviewsByBook(bookId));
    }

    // 6️⃣ Upload ảnh / trích dẫn
    @PostMapping("/{reviewId}/media")
    public ResponseEntity<ReviewResponse> uploadMedia(
            @PathVariable Long reviewId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(reviewService.uploadMedia(reviewId, user, file));
    }

    @GetMapping("/{reviewId}/comments")
    public ResponseEntity<List<CommentResponse>> getReviewComments(
            @PathVariable Long reviewId) {
        return ResponseEntity.ok(reviewService.getComments(reviewId));
    }
}
