package com.bookhup.service.impl;

import com.bookhup.model.BookReview;
import com.bookhup.model.Comment;
import com.bookhup.model.Post;
import com.bookhup.model.User;
import com.bookhup.repository.BookReviewRepository;
import com.bookhup.repository.CommentRepository;
import com.bookhup.repository.PostRepository;
import com.bookhup.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final BookReviewRepository reviewRepository;

    // ------------------ CREATE COMMENT ------------------
    public Comment addCommentToPost(Long postId, User user, String content, Long parentId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Comment comment = Comment.builder()
                .post(post)
                .user(user)
                .content(content)
                .parentId(parentId)
                .likesCount(0)
                .createdAt(LocalDateTime.now())
                .build();
        postRepository.markDirty(postId);

        return commentRepository.save(comment);
    }

    public Comment addCommentToReview(Long reviewId, User user, String content, Long parentId) {
        BookReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        Comment comment = Comment.builder()
                .review(review)
                .user(user)
                .content(content)
                .parentId(parentId)
                .likesCount(0)
                .createdAt(LocalDateTime.now())
                .build();

        return commentRepository.save(comment);
    }

    // ------------------ GET COMMENTS ------------------
    public List<Comment> getCommentsByPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        return commentRepository.findByPost(post);
    }

    public List<Comment> getCommentsByReview(Long reviewId) {
        BookReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        return commentRepository.findByReview(review);
    }

    public List<Comment> getReplies(Long parentId) {
        return commentRepository.findByParentId(parentId);
    }

    // ------------------ UPDATE COMMENT ------------------
    public Comment updateComment(Long commentId, User user, String newContent) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        // chỉ chủ comment hoặc admin được update
        if (!comment.getUser().getUserId().equals(user.getUserId()) && !user.isAdmin()) {
            throw new RuntimeException("No permission to update comment");
        }

        comment.setContent(newContent);
        return commentRepository.save(comment);
    }

    // ------------------ DELETE COMMENT ------------------
    public void deleteComment(Long commentId, User user) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getUser().getUserId().equals(user.getUserId()) && !user.isAdmin()) {
            throw new RuntimeException("No permission to delete comment");
        }

        commentRepository.delete(comment);
    }
}
