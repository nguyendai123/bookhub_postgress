package com.bookhup.repository;

import com.bookhup.dto.response.post.PostFeedProjection;
import com.bookhup.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByOrderByCreatedAtDesc();

    @Query("""
            SELECT p FROM Post p
                        WHERE p.lastHashtagScannedAt IS NULL
                        OR p.updatedAt > p.lastHashtagScannedAt
            """)
    List<Post> findPostsNeedScan();

    @Query(value = """
            SELECT *
            FROM post p
            WHERE p.score_dirty = TRUE
            ORDER BY
                (
                    (p.trending_score * 2.0) +
                    (p.views * 0.1) +
                    GREATEST(
                        0,
                        1000 - (EXTRACT(EPOCH FROM (NOW() - p.created_at)) / 60.0 * 0.5)
                    )
                ) DESC
            """,
            nativeQuery = true)
    List<Post> findDirtyPostsOrderByPriority(Pageable pageable);

    @Query(value = """
            WITH user_actions AS (
                    SELECT
                        CAST(
                            COALESCE(
                                    metadata ->> 'postId',
                                    metadata -> 'req' ->> 'targetId'
                            ) AS BIGINT
                        ) AS post_id,
                        action_type
                    FROM user_behavior_log
                    WHERE user_id = :userId
            )
            SELECT 
                p.post_id AS postId,
                p.book_id AS bookId,
                p.user_id AS userId,
                p.content,
                p.image_url AS imageUrl,
                p.likes_count AS likesCount,
                p.comments_count AS commentsCount,
                p.shares_count AS sharesCount,
                p.share_of AS shareOf,
                p.views AS views,
                p.trending_score AS trendingScore,
                p.updated_at AS updatedAt,
                p.hashtags AS hashtags,
                
                rp.reading_status AS readingStatus,
                rp.current_page AS currentPage,
                rp.total_pages AS totalPages,
                rp.percent_done AS percentDone,
                
                 u.username AS userName,
                 u.avatar_url AS userAvatar,
                CASE
                    WHEN EXISTS(
                        SELECT 1 FROM likes l
                        WHERE l.post_id = p.post_id
                        AND l.user_id = :userId
                    ) THEN 1
                    ELSE 0
                END AS isLiked,

                -- final_score formula
                (
                    :wRecent * ( COALESCE(ua.views,0) * 0.4 + COALESCE(ua.likes,0) * 0.6 )
                    + :wFollowing * (
                            CASE WHEN f.follow_id IS NOT NULL THEN 1.0 ELSE 0.3 END
                      )
                    + :wTrending * COALESCE(p.trending_score, 0)
                ) AS finalScore

            FROM posts p
            -- JOIN bảng tiến độ đọc theo user + sách
            LEFT JOIN reading_progress rp
                ON rp.book_id = p.book_id
                AND rp.user_id = p.user_id
            LEFT JOIN users u ON u.user_id = p.user_id
            -- hoạt động user xem/like post
            -- Join user_actions để lấy view/like count
            LEFT JOIN (
                SELECT
                    post_id,
                    SUM(CASE WHEN action_type = 'POST_VIEW' THEN 1 ELSE 0 END) AS views,
                    SUM(CASE WHEN action_type = 'POST_LIKE' THEN 1 ELSE 0 END) AS likes     
                FROM user_actions
                GROUP BY post_id
            ) ua ON ua.post_id = p.post_id

            -- follow: user xem có follow tác giả hay không
            LEFT JOIN follows f 
              ON f.user_id = :userId 
             AND f.follow_user_id = p.user_id

            ORDER BY finalScore DESC
            """,
            countQuery = "SELECT COUNT(*) FROM posts p",
            nativeQuery = true)
    Page<PostFeedProjection> findFeedForUser(
            @Param("userId") Long userId,
            @Param("wRecent") double wRecent,
            @Param("wFollowing") double wFollowing,
            @Param("wTrending") double wTrending,
            Pageable pageable);

    @Query(value = """
            SELECT 
                p.post_id AS postId,
                p.user_id AS userId,
                p.book_id AS bookId,
                u.username AS userName,
                u.avatar_url AS userAvatar,

                p.content AS content,
                p.image_url AS imageUrl,
                p.hashtags AS hashtags,

                CASE
                    WHEN EXISTS(
                        SELECT 1 FROM likes l
                        WHERE l.post_id = p.post_id
                        AND l.user_id = :userId
                    ) THEN 1 ELSE 0
                END AS isLiked,
                p.likes_count AS likesCount,
                p.comments_count AS commentsCount,
                p.shares_count AS sharesCount,
                p.share_of AS shareOf,
                p.views AS views,
                p.updated_at AS updatedAt,

                rp.total_pages AS totalPages,
                rp.reading_status AS readingStatus,
                rp.current_page AS currentPage,
                rp.percent_done AS percentDone

            FROM posts p
            JOIN users u ON p.user_id = u.user_id
            LEFT JOIN reading_progress rp 
                   ON p.book_id = rp.book_id AND rp.user_id = p.user_id

            WHERE p.post_id = :postId
            """, nativeQuery = true)
    Optional<PostFeedProjection> findOriginalPost(
            @Param("postId") Long postId,
            @Param("userId") Long userId
    );


    // 1️⃣ Query phân trang – KHÔNG FETCH
    @Query("""
                SELECT p FROM Post p
                WHERE p.user.userId = :userId
                ORDER BY p.createdAt DESC
            """)
    Page<Post> findUserPosts(
            @Param("userId") Long userId,
            Pageable pageable
    );

    // 2️⃣ Fetch detail theo ID
    @Query("""
                SELECT p FROM Post p
                JOIN FETCH p.user
                LEFT JOIN FETCH p.book b
                LEFT JOIN FETCH b.readingProgresses
                WHERE p.postId IN :postIds
            """)
    List<Post> fetchPostsWithReadingProgress(
            @Param("postIds") List<Long> postIds
    );

    @Query("""
                select p.ownerId
                from Post p
                where p.postId = :postId
            """)
    Optional<Long> findOwnerId(@Param("postId") Long postId);

}
