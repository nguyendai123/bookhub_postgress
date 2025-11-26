package com.bookhup.service.impl;

import com.bookhup.dto.request.like.LikeRequest;
import com.bookhup.model.Like;
import com.bookhup.model.Post;
import com.bookhup.model.User;
import com.bookhup.repository.LikeRepository;
import com.bookhup.repository.PostRepository;
import com.bookhup.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepo;
    private final PostRepository postRepo;

    public String toggleLike(LikeRequest req, User user) {

        boolean existed = likeRepo
                .existsByUserUserIdAndTargetTypeAndTargetId(user.getUserId(), req.getTargetType(), req.getTargetId());

        if (existed) {
            return "Đã thích trước đó";
        }

        Like like = Like.builder()
                .user(user)
                .post(req.getTargetType().equals("POST") ? postRepo.findById(req.getTargetId()).orElse(null) : null)
                .targetType(req.getTargetType())
                .targetId(req.getTargetId())
                .createdAt(LocalDateTime.now())
                .build();

        likeRepo.save(like);

        // Nếu like bài viết thì tăng likeCount
        if ("POST".equals(req.getTargetType())) {
            Post p = postRepo.findById(req.getTargetId()).orElseThrow();
            p.setLikesCount(p.getLikesCount() + 1);
            postRepo.save(p);
        }

        return "Đã thích thành công";
    }
}
