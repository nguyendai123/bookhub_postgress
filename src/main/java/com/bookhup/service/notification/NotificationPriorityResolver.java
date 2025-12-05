package com.bookhup.service.notification;

import com.bookhup.model.NotificationPriority;
import com.bookhup.model.NotificationType;

public class NotificationPriorityResolver {

    public static NotificationPriority priorityOf(NotificationType type) {
        return switch (type) {

            // HIGHEST PRIORITY — tác động trực tiếp, cần hiển thị ngay
            case N001_REGISTER_SUCCESS,
                 N002_NEW_DEVICE_LOGIN,
                 N003_PASSWORD_CHANGED,
                 N004_FORGOT_PASSWORD,
                 N012_REPLY_COMMENT,
                 N016_TAG_IN_POST,
                 N053_TAG_IN_REVIEW,
                 N077_AI_ERROR,
                 N079_AI_REQUIRE_CONTEXT -> NotificationPriority.HIGHEST;

            // HIGH — quan trọng, cần được highlight
            case N011_COMMENT_ON_POST,
                 N021_BOOK_RELEASED,
                 N020_NEW_BOOK,
                 N050_FOLLOW_YOU,
                 N010_FOLLOWING_POSTED,
                 N061_NEW_FEATURE,
                 N062_TOS_UPDATED,
                 N073_AI_SUMMARY_READY,
                 N074_AI_HIGHLIGHT_READY -> NotificationPriority.HIGH;

            // MEDIUM — dùng hàng ngày, mức ưu tiên trung bình
            case N005_PROFILE_UPDATED,
                 N006_ACCOUNT_DISABLED,
                 N030_FOLLOWING_REVIEW,
                 N032_COMMENT_REVIEW,
                 N040_READING_REMINDER,
                 N041_FOLLOWING_START_READING,
                 N042_FOLLOWING_UPDATE_READING,
                 N043_FOLLOWING_FINISH_BOOK,
                 N044_SUGGEST_REVIEW,
                 N060_SYSTEM_MAINTENANCE,
                 N070_BOOK_SUGGESTION,
                 N072_TRENDING_BOOKS -> NotificationPriority.MEDIUM;

            // LOW — thông báo phụ
            case N015_LIKE_COMMENT,
                 N031_LIKE_REVIEW -> NotificationPriority.LOW;

            // LOWEST — tương tác spam nhiều, không quan trọng
            case N013_LIKE_POST -> NotificationPriority.LOWEST;

            default -> NotificationPriority.MEDIUM;
        };
    }
}

