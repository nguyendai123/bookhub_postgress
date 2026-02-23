package com.bookhup.service.impl;

import com.bookhup.dto.response.post.OriginalPostDto;
import com.bookhup.dto.response.post.PostFeedDto;
import com.bookhup.dto.response.post.PostFeedProjection;
import com.bookhup.model.*;
import com.bookhup.repository.*;
import com.bookhup.dto.request.post.PostRequest;
import com.bookhup.service.PostService;
import com.bookhup.service.ReadingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final BookRepository bookRepository;
    private final HashtagRepository hashtagRepo;
    private final FollowRepository followRepository;
    private final UserFeedWeightsRepository weightRepo;
    private final LikeRepository likeRepository;
    private final ReadingService readingService;

    @Scheduled(fixedRateString = "${scheduler.trending-updates}")
    public void processTrendingUpdates() {

        List<Post> dirtyPosts = postRepository.findDirtyPostsOrderByPriority(PageRequest.of(0, 400));

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
    public Page<PostFeedDto> getFeed(Long userId, int page, int size) {

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

        Page<PostFeedProjection> feedPage = postRepository.findFeedForUser(userId, wRecent, wFollowing, wTrending, pageable);
        return feedPage.map(p -> mapToFeedDto(p, userId));
    }

    private PostFeedDto mapToFeedDto(PostFeedProjection p,Long userId) {

        PostFeedDto dto = PostFeedDto.builder()
                .postId(p.getPostId())
                .bookId(p.getBookId())
                .content(p.getContent())
                .imageUrl(p.getImageUrl())
                .hashtags(p.getHashtags())
                .updatedAt(p.getUpdatedAt())
                .likesCount(p.getLikesCount())
                .commentsCount(p.getCommentsCount())
                .sharesCount(p.getSharesCount())
                .shareOf(p.getShareOf())
                .views(p.getViews())
                .userId(p.getUserId())
                .userName(p.getUserName())
                .userAvatar(p.getUserAvatar())
                .isLiked(p.getIsLiked())
                .totalPages(p.getTotalPages())
                .readingStatus(p.getReadingStatus())
                .currentPage(p.getCurrentPage())
                .percentDone(p.getPercentDone())
                .build();

        // nếu là bài chia sẻ → load bài gốc
        if (p.getShareOf() != null) {
            dto.setOriginalPost(loadOriginalPost(p.getShareOf(), userId));
        }

        return dto;
    }

    private OriginalPostDto loadOriginalPost(Long originalPostId, Long userId) {
        return postRepository.findOriginalPost(originalPostId, userId)
                .map(op -> OriginalPostDto.builder()
                        .postId(op.getPostId())
                        .userId(op.getUserId())
                        .bookId(op.getBookId())
                        .userName(op.getUserName())
                        .userAvatar(op.getUserAvatar())
                        .content(op.getContent())
                        .imageUrl(op.getImageUrl())
                        .hashtags(op.getHashtags())
                        .isLiked(op.getIsLiked())
                        .likesCount(op.getLikesCount())
                        .commentsCount(op.getCommentsCount())
                        .sharesCount(op.getSharesCount())
                        .shareOf(op.getShareOf())
                        .views(op.getViews())
                        .updatedAt(op.getUpdatedAt())
                        .totalPages(op.getTotalPages())
                        .readingStatus(op.getReadingStatus())
                        .currentPage(op.getCurrentPage())
                        .percentDone(op.getPercentDone())
                        .build()
                )
                .orElse(null);
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
    public PostFeedDto getPostIncluOrigin(Long postId, Long userId) {

        PostFeedProjection p = postRepository
                .findPostDetail(postId, userId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        return mapToFeedDto(p, userId);
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

    public Page<PostFeedDto> getUserPosts(Long userId, Pageable pageable) {

        // 1️⃣ Lấy PAGE ID
        Page<Post> page = postRepository.findUserPosts(userId, pageable);

        if (page.isEmpty()) {
            return Page.empty(pageable);
        }

        // 2️⃣ Lấy ID
        List<Long> postIds = page.getContent()
                .stream()
                .map(Post::getPostId)
                .toList();

        // 3️⃣ Fetch đầy đủ dữ liệu
        List<Post> fullPosts =
                postRepository.fetchPostsWithReadingProgress(postIds);

        // 4️⃣ Map theo ID
        Map<Long, Post> postMap = fullPosts.stream()
                .collect(Collectors.toMap(Post::getPostId, p -> p));

        // 5️⃣ Convert DTO theo thứ tự page ban đầu
        List<PostFeedDto> responses = page.getContent().stream()
                .map(p -> postMap.get(p.getPostId()))
                .map(p -> toDto(p, userId))
                .toList();

        return new PageImpl<>(responses, pageable, page.getTotalElements());
    }

    private PostFeedDto toDto(Post post, Long userId) {

        ReadingProgress rp = null;

        if (post.getBook() != null) {
            rp = post.getBook().getReadingProgresses()
                    .stream()
                    .filter(x -> x.getUser().getUserId().equals(userId))
                    .findFirst()
                    .orElse(null);
        }

        return PostFeedDto.builder()
                .postId(post.getPostId())
                .content(post.getContent())
                .updatedAt(post.getUpdatedAt())

                .totalPages(rp == null ? null : rp.getTotalPages().toString())
                .currentPage(rp == null ? null : rp.getCurrentPage().toString())
                .percentDone(rp == null ? null : rp.getPercentDone().toString())
                .readingStatus(rp == null ? null : rp.getReadingStatus().name())
                .postId(post.getPostId())
                .bookId(post.getBook().getBookId())
                .content(post.getContent())
                .imageUrl(post.getImageUrl())
                .hashtags(post.getHashtags())
                .updatedAt(post.getUpdatedAt())
                .likesCount(post.getLikesCount())
                .commentsCount(post.getCommentsCount())
                .sharesCount(post.getSharesCount())
                .shareOf(post.getShareOf())
                .views(post.getViews())
                .userId(post.getUser().getUserId())
                .userName(post.getUser().getUsername())
                .userAvatar(post.getUser().getAvatarUrl())
                .isLiked(likeRepository.existsByUserUserIdAndTargetTypeAndTargetId(userId, "POST", post.getPostId()) ? 1: 0)

                .build();
    }
}
