package com.bookhup.repository;

import com.bookhup.model.BroadcastNotification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BroadcastNotificationRepository extends JpaRepository<BroadcastNotification, Long> {

    @Query("""
        SELECT b
        FROM BroadcastNotification b
        WHERE b.createdAt > :lastSeen
          AND NOT EXISTS (
              SELECT 1 FROM UserNotification u
              WHERE u.userId = :userId
                AND u.broadcastId = b.id
          )
        ORDER BY b.createdAt DESC
        """)
    List<BroadcastNotification> findUnreadBroadcasts(
            @Param("userId") Long userId,
            @Param("lastSeen") LocalDateTime lastSeen,
            Pageable pageable
    );
}

