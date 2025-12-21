package com.bookhup.repository;

import com.bookhup.dto.response.ai.bookTrending.TrendingBookProjection;
import com.bookhup.model.ActionType;
import com.bookhup.model.UserBehaviorLog;
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
                  AND JSON_UNQUOTE(JSON_EXTRACT(l.metadata, '$.post_id')) = :postId
            """, nativeQuery = true)
    long countByUserAndActionTypeAndPostId(
            @Param("userId") Long userId,
            @Param("actionType") ActionType actionType,
            @Param("postId") Long postId
    );

    @Query(value = """
        SELECT 
            CAST(JSON_UNQUOTE(JSON_EXTRACT(metadata, '$.id')) AS UNSIGNED) AS bookId,
            COUNT(*) AS count
        FROM user_behavior_log
        WHERE action_type IN (:actions)
        GROUP BY bookId
        ORDER BY count DESC
        """,
            nativeQuery = true)
    List<TrendingBookProjection> findTrendingBooks(
            @Param("actions") List<String> actions,
            Pageable pageable
    );

}
