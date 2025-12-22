package com.bookhup.model;

public enum TargetType {
    SELF,           // chính user
    SINGLE_USER,    // 1 user khác
    MULTI_USERS,    // nhiều user (followers, mentions)
    ALL_USERS,      // system broadcast
    NONE            // chỉ log, không gửi noti
}

