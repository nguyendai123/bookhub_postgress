package com.bookhup.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public class TargetResult {

    private TargetType type;
    private Long targetUserId;        // SINGLE
    private Set<Long> targetUserIds;  // MULTI

    public static TargetResult self(Long userId) {
        return new TargetResult(TargetType.SELF, userId, null);
    }

    public static TargetResult single(Long userId) {
        return new TargetResult(TargetType.SINGLE_USER, userId, null);
    }

    public static TargetResult multi(Set<Long> userIds) {
        return new TargetResult(TargetType.MULTI_USERS, null, userIds);
    }

    public static TargetResult all() {
        return new TargetResult(TargetType.ALL_USERS, null, null);
    }

    public static TargetResult none() {
        return new TargetResult(TargetType.NONE, null, null);
    }
}

