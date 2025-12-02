package com.bookhup.service;

import com.bookhup.dto.request.comment.CommentRequest;
import com.bookhup.dto.request.review.ReviewRequest;
import com.bookhup.dto.response.comment.CommentResponse;
import com.bookhup.dto.response.review.ReviewResponse;
import com.bookhup.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BookReviewService {
    // 1️⃣ CREATE REVIEW
    ReviewResponse createReview(User user, ReviewRequest request);

    // 2️⃣ UPDATE REVIEW
    ReviewResponse updateReview(User user, Long reviewId, ReviewRequest req);

    // 3️⃣ DELETE REVIEW
    void deleteReview(User user, Long reviewId);

    // 4️⃣ GET one review
    ReviewResponse getReview(Long reviewId);

    // 5️⃣ GET reviews of a book
    List<ReviewResponse> getReviewsByBook(Long bookId);

    // 6️⃣ Upload media
    ReviewResponse uploadMedia(Long reviewId, User user, MultipartFile file);

    List<CommentResponse> getComments(Long reviewId);
}
