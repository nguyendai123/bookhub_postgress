package com.bookhup.service.impl;

import com.bookhup.model.Comment;
import com.bookhup.model.Post;
import com.bookhup.model.User;
import com.bookhup.repository.CommentRepository;
import com.bookhup.repository.PostRepository;
import com.bookhup.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public Comment createComment(Comment comment, User currentUser, Long postId, Long parentId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        comment.setPost(post);
        comment.setUser(currentUser);
        comment.setParentId(parentId);
        comment.setCreatedAt(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    public List<Comment> getCommentsByPost(Long postId) {
        return commentRepository.findByPostPostIdOrderByCreatedAtAsc(postId);
    }

    public Comment updateComment(Long commentId, Comment updatedComment, User currentUser) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        if (!comment.getUser().getUserId().equals(currentUser.getUserId()) && !currentUser.isAdmin()) {
            throw new AccessDeniedException("You don't have permission to edit this comment");
        }

        comment.setContent(updatedComment.getContent());
        comment.setTranslatedText(updatedComment.getTranslatedText());
        return commentRepository.save(comment);
    }

    public void deleteComment(Long commentId, User currentUser) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getUser().getUserId().equals(currentUser.getUserId()) && !currentUser.isAdmin()) {
            throw new AccessDeniedException("You don't have permission to delete this comment");
        }

        commentRepository.delete(comment);
    }
}
