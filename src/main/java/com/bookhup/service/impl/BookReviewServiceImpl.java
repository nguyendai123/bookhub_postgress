package com.bookhup.service.impl;

import com.bookhup.dto.request.review.ReviewRequest;
import com.bookhup.dto.response.comment.CommentResponse;
import com.bookhup.dto.response.review.ReviewResponse;
import com.bookhup.model.Book;
import com.bookhup.model.BookReview;
import com.bookhup.model.User;
import com.bookhup.repository.BookRepository;
import com.bookhup.repository.BookReviewRepository;
import com.bookhup.repository.CommentRepository;
import com.bookhup.repository.LikeRepository;
import com.bookhup.service.BookReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookReviewServiceImpl implements BookReviewService {

    private final BookReviewRepository reviewRepo;
    private final BookRepository bookRepo;
    private final CommentRepository commentRepo;

    // 1️⃣ CREATE REVIEW
    @Override
    public ReviewResponse createReview(User user, ReviewRequest request) {
        Book book = bookRepo.findById(request.getBookId())
                .orElseThrow(() -> new RuntimeException("Book not found"));

        BookReview review = BookReview.builder()
                .user(user)
                .book(book)
                .rating(request.getRating())
                .comment(request.getComment())
                .lang(request.getLang())
                .createdAt(LocalDateTime.now())
                .build();

        reviewRepo.save(review);
        return ReviewResponse.from(review);
    }

    // 2️⃣ UPDATE REVIEW
    @Override
    public ReviewResponse updateReview(User user, Long reviewId, ReviewRequest req) {
        BookReview review = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        if (!review.getUser().getUserId().equals(user.getUserId())
            && !user.isAdmin())
            throw new RuntimeException("Permission denied");

        review.setRating(req.getRating());
        review.setComment(req.getComment());
        review.setLang(req.getLang());

        return ReviewResponse.from(reviewRepo.save(review));
    }

    // 3️⃣ DELETE REVIEW
    @Override
    public void deleteReview(User user, Long reviewId) {
        BookReview review = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        if (!review.getUser().getUserId().equals(user.getUserId())
            && !user.isAdmin())
            throw new RuntimeException("Permission denied");

        reviewRepo.delete(review);
    }

    // 4️⃣ GET one review
    @Override
    public ReviewResponse getReview(Long reviewId) {
        return reviewRepo.findById(reviewId)
                .map(ReviewResponse::from)
                .orElseThrow(() -> new RuntimeException("Review not found"));
    }

    // 5️⃣ GET reviews of a book
    @Override
    public List<ReviewResponse> getReviewsByBook(Long bookId) {
        return reviewRepo.findByBook_BookIdOrderByCreatedAtDesc(bookId)
                .stream()
                .map(ReviewResponse::from)
                .toList();
    }

    // 6️⃣ Upload media
    @Override
    public ReviewResponse uploadMedia(Long reviewId, User user, MultipartFile file) {
        BookReview review = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        if (!review.getUser().getUserId().equals(user.getUserId()))
            throw new RuntimeException("Cannot upload media to others' review");

        String url = "/uploads/" + file.getOriginalFilename();

        review.setImageUrl(url);
        reviewRepo.save(review);

        return ReviewResponse.from(review);
    }

    @Override
    public List<CommentResponse> getComments(Long reviewId) {
        return commentRepo.findByReviewReviewIdOrderByCreatedAtAsc(reviewId)
                .stream()
                .map(CommentResponse::from)
                .toList();
    }
}

