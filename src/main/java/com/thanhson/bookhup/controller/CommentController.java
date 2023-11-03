package com.thanhson.bookhup.controller;

import com.thanhson.bookhup.model.Comment;
import com.thanhson.bookhup.model.Post;
import com.thanhson.bookhup.model.User;
import com.thanhson.bookhup.service.CommentService;
import com.thanhson.bookhup.service.PostService;
import com.thanhson.bookhup.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")

@RequestMapping("/api/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;
    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<Comment>> getCommentsByPostId(@PathVariable Long postId) {
        List<Comment> comments = commentService.findAllByPostId(postId);
        return ResponseEntity.ok(comments);
    }

    @PostMapping("/post/{postId}/user/{userId}")
    public ResponseEntity<Comment> saveComment(@PathVariable Long postId,
            @PathVariable Long userId,
            @RequestBody Comment comment) {
        Post post = postService.getPostById(postId);
        if (post == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        User user = userService.getUserById(userId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Comment savedComment = commentService.saveComment(postId, userId, comment);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedComment);
    }

    // @PostMapping("/comment")
    // public ResponseEntity<Comment> addComment(@RequestParam long postID,
    // @RequestParam long userID, @RequestBody @Valid Comment comment, BindingResult
    // result) {
    // if (result.hasErrors()) {
    // return ResponseEntity.badRequest().build();
    // }
    //
    // Post post = postService.getPostById(postID);
    // User user = userService.getUserById(userID);
    // if (post == null) {
    // return ResponseEntity.notFound().build();
    // }
    //
    // Comment comment1 = new Comment();
    // comment1.setContent(comment.getContent());
    // comment1.setUser(user);
    // comment1.setPost(post);
    // comment1.setCreateAt(LocalDateTime.now());
    //
    // commentService.save(comment1);
    //
    // return ResponseEntity.ok(comment);
    // }
}