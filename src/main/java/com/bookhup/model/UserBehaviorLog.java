package com.bookhup.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_behavior_log")
public class UserBehaviorLog {

    @Id
    @Column(name = "log_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID logId;

    @JoinColumn(name = "user_id")
    private Long userId;

    @JoinColumn(name = "target_user_id")
    private Long targetUserId;  // SINGLE

    @Column(length = 100)
    private String username;

    @Column(name = "action_type", length = 50)
    @Enumerated(EnumType.STRING)
    private ActionType actionType;

    @Column(name = "target_type", length = 50)
    @Enumerated(EnumType.STRING)
    private TargetType targetType;

    @JoinColumn(name = "target_userIds")
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Set<Long> targetUserIds; // MULTI

    @Lob
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> metadata;

    @Column(length = 50)
    private String device;

    @Column(length = 100)
    private String location;

    @Column(name = "ai_cluster_id")
    private String aiClusterId;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;
}
