package com.bookhup.service.impl;

import com.bookhup.dto.request.hashtag.PostHashtagRequest;
import com.bookhup.model.Hashtag;
import com.bookhup.model.Post;
import com.bookhup.model.User;
import com.bookhup.repository.HashtagRepository;
import com.bookhup.repository.PostRepository;
import com.bookhup.service.HashtagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HashtagServiceImpl implements HashtagService {

    private final HashtagRepository hashtagRepo;
    private final PostRepository postRepo;

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

            // Lưu vào bảng hashtags
            hashtagRepo.findByTagName(tag)
                    .orElseGet(() -> hashtagRepo.save(
                            Hashtag.builder()
                                    .tagName(tag)
                                    .createdAt(LocalDateTime.now())
                                    .build()
                    ));

            // Thêm vào bài viết (JSON)
            if (!post.getHashtags().contains(tag)) {
                post.getHashtags().add(tag);
            }
        }

        return postRepo.save(post);
    }
}

