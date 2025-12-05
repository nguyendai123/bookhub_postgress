package com.bookhup.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_behavior_log")
public class UserBehaviorLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long logId;

    @JoinColumn(name = "user_id")
    private Long userId;

    @Column(nullable = false, length = 100, unique = true)
    private String username;

    @Column(name = "action_type", length = 50)
    @Enumerated(EnumType.STRING)
    private ActionType actionType;

    @Lob
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
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
