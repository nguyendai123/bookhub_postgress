package com.bookhup.service.impl;

import com.bookhup.model.Post;
import com.bookhup.model.User;
import com.bookhup.repository.PostRepository;
import com.bookhup.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    public Post createPost(Post post, User currentUser) {
        post.setUser(currentUser);
        post.setCreatedAt(LocalDateTime.now());
        return postRepository.save(post);
    }

    public List<Post> getAllPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    public Post updatePost(Long postId, Post updatedPost, User currentUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (!post.getUser().getUserId().equals(currentUser.getUserId()) && !currentUser.isAdmin()) {
            throw new AccessDeniedException("You don't have permission to edit this post");
        }

        post.setContent(updatedPost.getContent());
        post.setImageUrl(updatedPost.getImageUrl());
        post.setTranslatedText(updatedPost.getTranslatedText());
        post.setHashtags(updatedPost.getHashtags());
        return postRepository.save(post);
    }

    public void deletePost(Long postId, User currentUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (!post.getUser().getUserId().equals(currentUser.getUserId()) && !currentUser.isAdmin()) {
            throw new AccessDeniedException("You don't have permission to delete this post");
        }

        postRepository.delete(post);
    }
}
