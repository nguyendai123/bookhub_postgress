package com.bookhup.repository;

import com.bookhup.dto.response.post.PostFeedProjection;
import com.bookhup.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query(value = """
    WITH user_actions AS (
        SELECT 
            CAST(JSON_UNQUOTE(JSON_EXTRACT(metadata, '$.post_id')) AS UNSIGNED) AS post_id,
            SUM(CASE WHEN action_type = 'POST_VIEW' THEN 1 ELSE 0 END) AS views,
            CASE WHEN SUM(CASE WHEN action_type = 'POST_LIKE' THEN 1 ELSE 0 END) > 0 THEN 1 ELSE 0 END AS likes
        FROM user_behavior_log
        WHERE user_id = :userId
        GROUP BY CAST(JSON_UNQUOTE(JSON_EXTRACT(metadata, '$.post_id')) AS UNSIGNED)
    )

    SELECT 
        p.post_id AS postId,
        p.user_id AS userId,
        p.content,
        p.image_url AS imageUrl,
        p.likes_count AS likesCount,
        p.comments_count AS commentsCount,
        p.shares_count AS sharesCount,
        p.views AS views,
        p.trending_score AS trendingScore,

        -- final_score formula
        (
            :wRecent * ( COALESCE(ua.views,0) * 0.4 + COALESCE(ua.likes,0) * 0.6 )
            + :wFollowing * (
                    CASE WHEN f.follow_id IS NOT NULL THEN 1.0 ELSE 0.3 END
              )
            + :wTrending * COALESCE(p.trending_score, 0)
        ) AS finalScore

    FROM posts p

    -- hoạt động user xem/like post
    LEFT JOIN user_actions ua ON ua.post_id = p.post_id

    -- follow: user xem có follow tác giả hay không
    LEFT JOIN follows f 
      ON f.user_id = :userId 
     AND f.follow_user_id = p.user_id

    ORDER BY finalScore DESC
    """,
            countQuery = "SELECT COUNT(*) FROM posts",
            nativeQuery = true)
    Page<PostFeedProjection> findFeedForUser(
            @Param("userId") Long userId,
            @Param("wRecent") double wRecent,
            @Param("wFollowing") double wFollowing,
            @Param("wTrending") double wTrending,
            Pageable pageable);

}
