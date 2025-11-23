package com.bookhup.service;

import com.bookhup.model.Comment;
import com.bookhup.model.Post;
import com.bookhup.model.User;

import java.util.List;

public interface CommentService {

    Comment createComment(Comment comment, User user, Long postId, Long parentId);

    List<Comment> getCommentsByPost(Long postId);

    Comment updateComment(Long commentId, Comment comment, User user);

    void deleteComment(Long commentId, User user);
}

