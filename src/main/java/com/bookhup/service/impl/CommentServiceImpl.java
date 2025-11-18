package com.bookhup.service.impl;

import com.bookhup.repository.CommentRepository;
import com.bookhup.model.Comment;
import com.bookhup.model.Post;
import com.bookhup.model.User;
import com.bookhup.service.CommentService;
import com.bookhup.service.PostService;
import com.bookhup.service.auth.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostService postService;
    private final UserService userService;

    @Override
    public List<Comment> findAllByPostId(long postID) {
        return commentRepository.findAllByPost_PostIDOrderByCreateAtDesc(postID);
    }

    @Override
    public Comment saveComment(Long postId, Long userId, Comment comment) {
        Post post = postService.getPostById(postId);
        User user = userService.getUserById(userId);

        comment.setPost(post);
        comment.setUser(user);
        comment.setCreateAt(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    @Override
    public void deleteCommentsByPost(Post post) {
        commentRepository.deleteByPost(post);
    }

}
