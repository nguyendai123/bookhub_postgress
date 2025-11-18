package com.bookhup.service;

import com.bookhup.model.Comment;
import com.bookhup.model.Post;

import java.util.List;

public interface CommentService {

    List<Comment> findAllByPostId(long postId);

    Comment saveComment(Long postId, Long userId, Comment comment);

    void deleteCommentsByPost(Post post);
}

