package com.bookhup.service.impl;

import com.bookhup.dto.request.share.ShareRequest;
import com.bookhup.model.Post;
import com.bookhup.model.Share;
import com.bookhup.model.User;
import com.bookhup.repository.PostRepository;
import com.bookhup.repository.ShareRepository;
import com.bookhup.service.ShareService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ShareServiceImpl implements ShareService {

    private final PostRepository postRepo;
    private final ShareRepository shareRepo;

    public Share sharePost(ShareRequest req, User user) {

        Post original = postRepo.findById(req.getPostId())
                .orElseThrow(() -> new RuntimeException("Bài viết không tồn tại"));

        // Tạo bài viết mới dạng share
        Post sharedPost = Post.builder()
                .user(user)
                .content(req.getContent())
                .translatedText(req.getTranslatedText())
                .imageUrl(req.getImageUrl())
                .hashtags(req.getHashtags())
                .shareOf(original.getPostId())
                .createdAt(LocalDateTime.now())
                .likesCount(0)
                .commentsCount(0)
                .sharesCount(0)
                .views(0)
                .build();

        postRepo.save(sharedPost);

        // Ghi record share
        Share share = Share.builder()
                .user(user)
                .post(original)
                .sharedAt(LocalDateTime.now())
                .build();

        // Tăng số lần share
        original.setSharesCount(original.getSharesCount() + 1);
        original.setScoreDirty(true);
        postRepo.save(original);

        return shareRepo.save(share);
    }
}

