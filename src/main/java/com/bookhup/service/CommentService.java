package com.bookhup.service;

import com.bookhup.dto.response.comment.CommentResponse;
import com.bookhup.dto.response.comment.CommentWithUserDTO;
import com.bookhup.model.Comment;
import com.bookhup.model.User;

import java.util.List;

public interface CommentService {

    List<CommentWithUserDTO> getCommentsByPost(Long postId);

    void deleteComment(Long commentId, User user);

    Comment addCommentToPost(Long postId, User currentUser, String content, Long parentId);

    Comment addCommentToReview(Long reviewId, User currentUser, String content, Long parentId);

    List<CommentResponse> getCommentsByReview(Long reviewId);

    List<Comment> getReplies(Long parentId);

    Comment updateComment(Long commentId, User currentUser, String content);
}

