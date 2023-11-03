package com.thanhson.bookhup.controller;

import com.thanhson.bookhup.exception.ResourceNotFoundException;
import com.thanhson.bookhup.model.*;
import com.thanhson.bookhup.repository.PostRepository;
import com.thanhson.bookhup.service.*;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class PostController {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PostService postService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private BookService bookService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private UserService userService;
    @Autowired
    private ProgressService progressService;

    @GetMapping("/posts")
    public ResponseEntity<List<Post>> getAllPosts() {
        List<Post> posts = postService.findAll();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/posts/view")
    public ResponseEntity<Object> viewPost(
            @RequestParam(required = false) Long postID) {

        if (postID == null) {
            Post newPost = new Post();
            return ResponseEntity.ok(newPost);
        }

        Post post = postService.getPostById(postID);
        if (post == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post not found");
        }

        List<Comment> allComments = commentService.findAllByPostId(postID);
        List<Like> allLikes = likeService.findAllByPostId(postID);
        Map<String, Object> response = new HashMap<>();
        response.put("post", post);
        response.put("allComments", allComments);
        response.put("allLikes", allLikes);

        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/posts/create/{userId}")
    public ResponseEntity<String> createPost(@PathVariable Long userId,
            @RequestBody @Valid Post post,
            BindingResult result) {
        User loggedInUser = userService.getUserById(userId);
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body("Invalid input data.");
        }

        Long selectedBookID = post.getBook().getBookID();
        Book selectedBook = bookService.getBookById(selectedBookID)
                .orElseThrow(() -> new ResourceNotFoundException("Selected book not found."));
        ; // Đảm bảo bạn đã cài đặt phương thức này

        if (selectedBook == null) {
            return ResponseEntity.badRequest().body("Selected book not found.");
        }

        post.setUser(loggedInUser);

        post.setCreateDate(LocalDateTime.now());

        post.setLikeCount(0);

        post.setBook(selectedBook); // Gán sách vào bài viết

        postService.save(post);
        return ResponseEntity.ok("Post created successfully.");
    }

    @PutMapping("/posts/update/{postID}")
    public ResponseEntity<String> updatePost(
            @PathVariable Long postID,
            @RequestBody @Valid Post updatedPost,
            BindingResult result) {

        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body("Invalid input data.");
        }

        Post existingPost = postService.getPostById(postID);
        if (existingPost == null) {
            return ResponseEntity.notFound().build();
        }

        existingPost.setContent(updatedPost.getContent());
        existingPost.setRating(updatedPost.getRating());

        postService.save(existingPost);

        return ResponseEntity.ok("Post updated successfully.");
    }

    @DeleteMapping("/posts/delete/{postID}")
    public ResponseEntity<String> deletePost(@PathVariable Long postID) {
        Post post = postService.getPostById(postID);
        if (post == null) {
            return ResponseEntity.notFound().build();
        }

        // You can add additional checks here if needed, such as checking user
        // Delete likes associated with the post
        likeService.deleteLikesByPostId(postID);

        // Delete comments associated with the post
        //commentService.deleteCommentsByPostId(postID);
        // authorization
        //progressService.delete();
        postService.delete(post);

        return ResponseEntity.ok("Post deleted successfully.");
    }

    @GetMapping("/posts/{postId}/liked-users")
    public ResponseEntity<List<String>> getLikedUserNames(@PathVariable Long postID) {
        Optional<Post> optionalPost = postRepository.findById(postID);

        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            List<User> likedUsers = post.getLikedUsers();
            List<String> likedUserNames = likedUsers.stream()
                    .map(User::getUsername)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(likedUserNames);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<String> likePost(@PathVariable Long postId, @RequestBody @Valid Like like) {
        Like createdLike = likeService.save(like);


        // Tìm bài viết
        Optional<Post> optionalPost = postRepository.findById(postId);

        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();

            // Tăng giá trị likeCount
            int currentLikeCount = post.getLikeCount();
            post.setLikeCount(currentLikeCount + 1);
            postRepository.save(post);
            return ResponseEntity.status(HttpStatus.CREATED).body(String.valueOf(createdLike));
            //return ResponseEntity.ok("Post liked successfully.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @PostMapping("/posts/{postId}/{userId}/dislike")
    public ResponseEntity<String> disLikePost(@PathVariable Long postId,@PathVariable Long userId ) {
    Post post1 = postService.getPostById(postId);
    User user = userService.getUserById(userId);

        Like like = likeService.getLikeByUserIdAndPostId( post1,  user);
        if (like != null) {
            likeService.deleteLike(like.getLikeID());

            // Tìm bài viết
            Optional<Post> optionalPostDisLike = postRepository.findById(postId);

            if (optionalPostDisLike.isPresent()) {
                Post post = optionalPostDisLike.get();

                // Tăng giá trị likeCount
                int currentLikeCount = post.getLikeCount();
                post.setLikeCount(currentLikeCount - 1);
                postRepository.save(post);

                return ResponseEntity.ok("Post disliked successfully.");
            } else {
                return ResponseEntity.notFound().build();
            }
        }
        else {
            // Handle like not found
            return ResponseEntity.notFound().build();
        }
    }
}
