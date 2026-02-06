package com.bookhup.repository;

import com.bookhup.dto.response.ai.bookTrending.TrendingBookProjection;
import com.bookhup.model.ActionType;
import com.bookhup.model.UserBehaviorLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserBehaviorLogRepository extends JpaRepository<UserBehaviorLog, Long> {
    @Query(value = """
            SELECT COUNT(*) 
            FROM user_behavior_log l
            WHERE l.user_id = :userId
              AND l.action_type = :actionType
              AND (l.metadata ->> 'post_id')::BIGINT = :postId
            """, nativeQuery = true)
    long countByUserAndActionTypeAndPostId(
            @Param("userId") Long userId,
            @Param("actionType") ActionType actionType,
            @Param("postId") Long postId
    );

    @Query(value = """
    SELECT 
        (metadata ->> 'bookId')::BIGINT AS bookId,
        COUNT(*) AS count
    FROM user_behavior_log
    WHERE action_type IN (:actions)
      AND jsonb_exists(metadata, 'bookId')
    GROUP BY (metadata ->> 'bookId')::BIGINT
    ORDER BY count DESC
    """,
            countQuery = """
        SELECT COUNT(DISTINCT (metadata ->> 'bookId')::BIGINT)
        FROM user_behavior_log
        WHERE action_type IN (:actions)
          AND jsonb_exists(metadata, 'bookId')
        """,
            nativeQuery = true)
    Page<TrendingBookProjection> findTrendingBooks(
            @Param("actions") List<String> actions,
            Pageable pageable
    );




}
