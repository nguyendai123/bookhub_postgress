package com.bookhup.repository;

import com.bookhup.model.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByOrderByCreatedAtDesc();

    @Query("""
            SELECT p FROM Post p
                        WHERE p.lastHashtagScannedAt IS NULL
                        OR p.updatedAt > p.lastHashtagScannedAt
                       """)
    List<Post> findPostsNeedScan();

    @Modifying
    @Query("UPDATE Post p SET p.scoreDirty = true WHERE p.postId = :postId")
    void markDirty(Long postId);

    @Query("""
            SELECT p FROM Post p 
            WHERE p.scoreDirty = true
            ORDER BY 
                (p.trendingScore * 2 + p.views * 0.1 + 
                GREATEST(0, 1000 - TIMESTAMPDIFF(MINUTE, p.createdAt, NOW()) * 0.5)) DESC
            """)
    List<Post> findDirtyPostsOrderByPriority(Pageable pageable);

}
