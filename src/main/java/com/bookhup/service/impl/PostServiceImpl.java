package com.bookhup.service.impl;

import com.bookhup.dto.response.post.PostFeedProjection;
import com.bookhup.model.Book;
import com.bookhup.model.Post;
import com.bookhup.model.User;
import com.bookhup.model.UserFeedWeights;
import com.bookhup.repository.*;
import com.bookhup.dto.request.post.PostRequest;
import com.bookhup.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final BookRepository bookRepository;
    private final HashtagRepository hashtagRepo;
    private final FollowRepository followRepository;
    private final UserFeedWeightsRepository weightRepo;

    @Scheduled(fixedRate = 600000) // chạy mỗi phút
    public void processTrendingUpdates() {

        List<Post> dirtyPosts = postRepository.findDirtyPostsOrderByPriority(PageRequest.of(0, 10));

        for (Post p : dirtyPosts) {

            double score =
                    (p.getLikesCount() / 10.0) +
                            (p.getSharesCount() / 5.0) +
                            (p.getCommentsCount() / 3.0);

            p.setTrendingScore(score);
            p.setLastScoreUpdate(LocalDateTime.now());
            p.setScoreDirty(false);
        }

        postRepository.saveAll(dirtyPosts);
    }


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
    @Transactional(readOnly = true)
    public Page<PostFeedProjection> getFeed(Long userId, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        double wRecent = 0.5;
        double wFollowing = 0.3;
        double wTrending = 0.2;

        Optional<UserFeedWeights> weight = weightRepo.findById(userId);
        if(weight.isPresent()) {
            wRecent = weight.get().getWRecentInteraction();
            wFollowing = weight.get().getWFollowing();
            wTrending = weight.get().getWTrending();
        }

        return postRepository.findFeedForUser(userId, wRecent, wFollowing, wTrending, pageable);
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
