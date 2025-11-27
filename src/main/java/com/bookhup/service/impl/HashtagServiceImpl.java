package com.bookhup.service.impl;

import com.bookhup.dto.request.hashtag.PostHashtagRequest;
import com.bookhup.model.Hashtag;
import com.bookhup.model.Post;
import com.bookhup.model.User;
import com.bookhup.repository.HashtagRepository;
import com.bookhup.repository.PostRepository;
import com.bookhup.service.HashtagService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HashtagServiceImpl implements HashtagService {

    private final HashtagRepository hashtagRepo;
    private final PostRepository postRepo;
    private final PostRepository postRepository;
    private final HashtagRepository hashtagRepository;

    @Scheduled(fixedRateString = "${scheduler.hashtag.rate}")
    @Transactional
    public void scanHashtags() {
        List<Post> posts = postRepository.findPostsNeedScan();

        for (Post post : posts) {
            List<String> tags = post.getHashtags();

            if (tags != null) {
                for (String tag : tags) {
                    tag = tag.trim().toLowerCase();

                    String finalTag = tag;
                    Hashtag h = hashtagRepository.findByTagName(tag)
                            .orElseGet(() -> hashtagRepository.save(
                                    Hashtag.builder()
                                            .tagName(finalTag)
                                            .usageCount(0)
                                            .createdAt(LocalDateTime.now())
                                            .build()
                            ));

                    h.setUsageCount(h.getUsageCount() + 1);
                    h.setLastUsedAt(LocalDateTime.now());
                    hashtagRepository.save(h);
                }
            }

            post.setLastHashtagScannedAt(LocalDateTime.now());
            postRepository.save(post);
        }
    }

    public Post addHashtags(PostHashtagRequest req, User currentUser) {

        // 1. Lấy post từ DB
        Post post = postRepo.findById(req.getPostId())
                .orElseThrow(() -> new RuntimeException("Bài viết không tồn tại"));

        // 2. Kiểm tra quyền
        boolean isOwner = post.getUser().getUserId().equals(currentUser.getUserId());


        if (!isOwner && !currentUser.isAdmin()) {
            throw new RuntimeException("Bạn không có quyền chỉnh sửa hashtag của bài viết này");
        }

        // Gộp danh sách hashtag
        List<String> newTags = req.getHashtags();

        if (post.getHashtags() == null)
            post.setHashtags(new ArrayList<>());

        for (String tag : newTags) {
            // Thêm vào bài viết (JSON)
            if (!post.getHashtags().contains(tag)) {
                post.getHashtags().add(tag);
            }
        }
        post.setUpdatedAt(LocalDateTime.now());
        return postRepo.save(post);
    }
}

