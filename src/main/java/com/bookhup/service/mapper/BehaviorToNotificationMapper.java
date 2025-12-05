package com.bookhup.service.mapper;

import com.bookhup.model.ActionType;
import com.bookhup.model.NotificationType;
import org.springframework.stereotype.Component;

@Component
public class BehaviorToNotificationMapper {

    public NotificationType map(ActionType action) {
        if (action == null) return null;
        return switch (action) {

            // ================= AUTH =================
            case AUTH_REGISTER -> NotificationType.N001_REGISTER_SUCCESS;

            case AUTH_LOGIN -> NotificationType.N002_NEW_DEVICE_LOGIN;

            case AUTH_RESET_PASSWORD -> NotificationType.N004_FORGOT_PASSWORD;

            case USER_UPDATE_PROFILE -> NotificationType.N005_PROFILE_UPDATED;

            // ================= FOLLOW =================
            case USER_FOLLOW -> NotificationType.N050_FOLLOW_YOU;

            // ================= POSTS =================
            case POST_CREATE -> NotificationType.N010_FOLLOWING_POSTED;

            case COMMENT_CREATE_POST -> NotificationType.N011_COMMENT_ON_POST;

            case COMMENT_CREATE_REVIEW -> NotificationType.N032_COMMENT_REVIEW;

            case COMMENT_UPDATE -> NotificationType.N012_REPLY_COMMENT;

            case POST_LIKE -> NotificationType.N013_LIKE_POST;

            case POST_SHARE -> NotificationType.N014_SHARE_POST;

            case COMMENT_LIKE -> NotificationType.N015_LIKE_COMMENT;

            // tag bạn bè trong post
            case POST_UPDATE -> NotificationType.N016_TAG_IN_POST;

            // ================= REVIEWS =================
            case REVIEW_CREATE -> NotificationType.N030_FOLLOWING_REVIEW;

            case REVIEW_UPDATE -> NotificationType.N053_TAG_IN_REVIEW;

            case REVIEW_DELETE -> null;

            case REVIEW_VIEW -> null;

            case REVIEW_ADD_MEDIA -> null;

            case REVIEW_LIST_BY_BOOK -> null;


            case BOOKREVIEW_LIKE -> NotificationType.N031_LIKE_REVIEW;

            // ================= BOOK =================
            case ADMIN_BOOK_CREATE -> NotificationType.N020_NEW_BOOK;

            case BOOK_VIEW_DETAIL -> NotificationType.N021_BOOK_RELEASED;

            // ================= READING =================
            case READING_ADD -> NotificationType.N041_FOLLOWING_START_READING;

            case READING_PROGRESS_UPDATE -> NotificationType.N042_FOLLOWING_UPDATE_READING;

            // ================= HASHTAG CLICK =================
            case HASHTAG_CLICK -> null; // không gửi noti

            // ================= SYSTEM =================
            case NOTIFICATION_VIEW -> null;

            default -> null;
        };
    }
}

