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

    @Override
    public Post createPost(Post request, User currentUser) {
        Post post = Post.builder()
                .user(currentUser)
                .content(request.getContent())
                .translatedText(request.getTranslatedText())
                .imageUrl(request.getImageUrl())
                .hashtags(request.getHashtags())
                .book(request.getBook())
                .shareOf(request.getShareOf())
                .createdAt(LocalDateTime.now())
                .likesCount(0)
                .commentsCount(0)
                .sharesCount(0)
                .views(0)
                .build();
        return postRepository.save(post);
    }

    @Override
    public List<Post> getAllPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    public Post getPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
    }

    @Override
    public Post updatePost(Long postId, Post request, User currentUser) {
        Post post = getPost(postId);

        if (!post.getUser().getUserId().equals(currentUser.getUserId()) && !currentUser.isAdmin()) {
            throw new AccessDeniedException("You don't have permission to edit this post");
        }

        post.setContent(request.getContent());
        post.setTranslatedText(request.getTranslatedText());
        post.setImageUrl(request.getImageUrl());
        post.setHashtags(request.getHashtags());
        post.setBook(request.getBook());

        return postRepository.save(post);
    }

    @Override
    public void deletePost(Long postId, User currentUser) {
        Post post = getPost(postId);

        if (!post.getUser().getUserId().equals(currentUser.getUserId()) && !currentUser.isAdmin()) {
            throw new AccessDeniedException("You don't have permission to delete this post");
        }

        postRepository.delete(post);
    }
}
