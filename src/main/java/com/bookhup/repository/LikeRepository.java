package com.bookhup.repository;

import com.bookhup.model.Like;
import com.bookhup.model.Post;
import com.bookhup.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    Like findByUserAndPost(User user, Post post);

    @Modifying
    @Transactional
    @Query("DELETE FROM Like l WHERE l.post.postId = :postId")
    void deleteByPostId(Long postId);

    boolean existsByUserUserIdAndTargetTypeAndTargetId(Long userId, String targetType, Long targetId);

    Optional<Like> findByUserUserIdAndTargetTypeAndTargetId(Long userId, String type, Long targetId);

    boolean existsByUserUserIdAndBookReviewReviewId(Long userId, Long reviewId);

    void deleteByUserUserIdAndBookReviewReviewId(Long userId, Long reviewId);
}
