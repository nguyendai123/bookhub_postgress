package com.bookhup.repository;

import com.bookhup.model.ActionType;
import com.bookhup.model.UserBehaviorLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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

}
