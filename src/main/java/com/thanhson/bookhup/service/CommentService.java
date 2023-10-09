package com.thanhson.bookhup.service;

import com.thanhson.bookhup.model.Comment;
import com.thanhson.bookhup.model.Post;
import com.thanhson.bookhup.model.User;
import com.thanhson.bookhup.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostService postService;


    @Autowired
    private UserService userService;


    public List<Comment> findAllByPostId(long postID) {
        return commentRepository.findAllByPost_PostIDOrderByCreateAtDesc(postID);
    }

    public Comment saveComment(Long postId, Long userId, Comment comment) {
        Post post = postService.getPostById(postId);
        User user = userService.getUserById(userId);

        comment.setPost(post);
        comment.setUser(user);
        comment.setCreateAt(LocalDateTime.now());
        return commentRepository.save(comment);
    }
}
