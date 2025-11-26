package com.bookhup.service.impl;

import com.bookhup.dto.request.like.LikeRequest;
import com.bookhup.model.*;
import com.bookhup.repository.BookReviewRepository;
import com.bookhup.repository.CommentRepository;
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
    private final CommentRepository commentRepo;
    private final BookReviewRepository bookReviewRepo;

    @Override
    public String toggleLike(LikeRequest req, User user) {

        boolean existed = likeRepo.existsByUserUserIdAndTargetTypeAndTargetId(
                user.getUserId(), req.getTargetType(), req.getTargetId()
        );

        if (existed) {
            return "Bạn đã thích trước đó!";
        }

        Like like = Like.builder()
                .user(user)
                .targetType(req.getTargetType())
                .targetId(req.getTargetId())
                .createdAt(LocalDateTime.now())
                .build();

        switch (req.getTargetType()) {

            case "POST" -> {
                Post p = postRepo.findById(req.getTargetId())
                        .orElseThrow(() -> new RuntimeException("Post không tồn tại"));

                like.setPost(p);
                p.setLikesCount(p.getLikesCount() + 1);
                postRepo.save(p);
            }

            case "COMMENT" -> {
                Comment c = commentRepo.findById(req.getTargetId())
                        .orElseThrow(() -> new RuntimeException("Comment không tồn tại"));

                like.setComment(c);
                c.setLikesCount(c.getLikesCount() + 1);
                commentRepo.save(c);
            }

            case "BOOKREVIEW" -> {
                BookReview r = bookReviewRepo.findById(req.getTargetId())
                        .orElseThrow(() -> new RuntimeException("BOOKREVIEW không tồn tại"));

                like.setBookReview(r);
                r.setLikesCount(r.getLikesCount() + 1);
                bookReviewRepo.save(r);
            }

            default -> throw new RuntimeException("targetType không hợp lệ! Chỉ chấp nhận POST, COMMENT, BOOKREVIEW");
        }

        likeRepo.save(like);

        return "Like thành công!";
    }
}
