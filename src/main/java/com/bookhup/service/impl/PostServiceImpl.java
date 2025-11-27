package com.bookhup.service.impl;

import com.bookhup.model.Book;
import com.bookhup.model.Hashtag;
import com.bookhup.model.Post;
import com.bookhup.model.User;
import com.bookhup.repository.BookRepository;
import com.bookhup.repository.HashtagRepository;
import com.bookhup.repository.PostRepository;
import com.bookhup.dto.request.post.PostRequest;
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
    private final BookRepository bookRepository;
    private final HashtagRepository hashtagRepo;

    @Override
    public Post createPost(PostRequest request, User currentUser) {

        Book book = null;

        if (request.getBookId() != null) {
            book = bookRepository.findById(request.getBookId())
                    .orElseThrow(() -> new RuntimeException("Book not found"));
        }

        Post post = Post.builder()
                .user(currentUser)
                .content(request.getContent())
                .translatedText(request.getTranslatedText())
                .imageUrl(request.getImageUrl())
                .hashtags(request.getHashtags())
                .book(book)
                .shareOf(request.getShareOf())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
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
    public Post updatePost(Long postId, PostRequest request, User currentUser) {
        Post post = getPost(postId);

        if (!post.getUser().getUserId().equals(currentUser.getUserId()) && !currentUser.isAdmin()) {
            throw new AccessDeniedException("You don't have permission to edit this post");
        }

        Book book = null;
        if (request.getBookId() != null) {
            book = bookRepository.findById(request.getBookId())
                    .orElseThrow(() -> new RuntimeException("Book not found"));
        }

        post.setContent(request.getContent());
        post.setTranslatedText(request.getTranslatedText());
        post.setImageUrl(request.getImageUrl());
        post.setHashtags(request.getHashtags());
        post.setBook(book);
        post.setUpdatedAt(LocalDateTime.now());

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
