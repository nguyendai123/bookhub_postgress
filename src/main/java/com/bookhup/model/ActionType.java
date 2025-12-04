package com.bookhup.model;

import lombok.Getter;
import org.springframework.http.HttpMethod;

import java.util.regex.Pattern;

@Getter
public enum ActionType {

    // ================= AUTH =================
    AUTH_LOGIN("/api/auth/login", HttpMethod.POST),
    AUTH_LOGOUT("/api/auth/logout", HttpMethod.POST),
    AUTH_REGISTER("/api/auth/register", HttpMethod.POST),
    AUTH_RESET_PASSWORD("/api/auth/reset-password", HttpMethod.POST),

    // ================= BOOK =================
    BOOK_SEARCH("/api/books/search", HttpMethod.GET),
    BOOK_VIEW_DETAIL("/api/books/{id}", HttpMethod.GET),

    // ================= ADMIN BOOK =================
    ADMIN_BOOK_CREATE("/api/admin/books", HttpMethod.POST),

    // ================= AUTHORS =================
    AUTHOR_LIST("/api/authors", HttpMethod.GET),
    AUTHOR_CREATE("/api/authors", HttpMethod.POST),

    // ================= POSTS =================
    POST_LIST("/api/posts", HttpMethod.GET),
    POST_CREATE("/api/posts", HttpMethod.POST),
    POST_VIEW("/api/posts/{id}", HttpMethod.GET),
    POST_UPDATE("/api/posts/{id}", HttpMethod.PUT),
    POST_DELETE("/api/posts/{id}", HttpMethod.DELETE),

    POST_ALL_FEEDS("/api/posts/all-feeds", HttpMethod.GET),
    POST_SHARE("/api/share", HttpMethod.POST),

    POST_CLICK_IMAGE("/api/posts/image/click/{id}", HttpMethod.POST),
    POST_CLICK_COMMENT_SECTION("/api/posts/comments/click/{id}", HttpMethod.POST),

    // ================= LIKE =================
    COMMENT_LIKE("/api/like", HttpMethod.POST),
    BOOKREVIEW_LIKE("/api/like", HttpMethod.POST),
    POST_LIKE("/api/like", HttpMethod.POST),

    COMMENT_UNLIKE("/api/unlike", HttpMethod.POST),
    BOOKREVIEW_UNLIKE("/api/unlike", HttpMethod.POST),
    POST_UNLIKE("/api/unlike", HttpMethod.POST),

    // ================= HASHTAGS =================
    HASHTAG_CLICK("/api/hashtags/add", HttpMethod.POST),

    // ================= GENRES =================
    GENRE_LIST("/api/genres", HttpMethod.GET),
    GENRE_CREATE("/api/genres", HttpMethod.POST),

    // ================= USERS =================
    USER_LIST("/api/users", HttpMethod.GET),
    USER_DELETE("/api/users/delete/{id}", HttpMethod.DELETE),
    USER_UPDATE_PROFILE("/api/users/profile/update", HttpMethod.PUT),

    // ================= FOLLOW =================
    FOLLOW_CHECK("/api/follow/check", HttpMethod.GET),
    USER_FOLLOW("/api/follow/{id}", HttpMethod.POST),
    USER_UNFOLLOW("/api/follow/{id}", HttpMethod.DELETE),
    FOLLOWERS_LIST("/api/follow/{id}/followers", HttpMethod.GET),
    FOLLOWING_LIST("/api/follow/{id}/following", HttpMethod.GET),

    // ================= REVIEWS =================
    REVIEW_CREATE("/api/reviews", HttpMethod.POST),
    REVIEW_LIST_BY_BOOK("/api/reviews/book/{id}", HttpMethod.GET),
    REVIEW_VIEW("/api/reviews/{id}", HttpMethod.GET),
    REVIEW_UPDATE("/api/reviews/{id}", HttpMethod.PUT),
    REVIEW_DELETE("/api/reviews/{id}", HttpMethod.DELETE),
    REVIEW_COMMENT_LIST("/api/reviews/{id}/comments", HttpMethod.GET),
    REVIEW_ADD_MEDIA("/api/reviews/{id}/media", HttpMethod.POST),

    // ================= COMMENTS =================
    COMMENT_LIST_POST("/api/comments/post/{id}", HttpMethod.GET),
    COMMENT_CREATE_POST("/api/comments/post/{id}", HttpMethod.POST),

    COMMENT_LIST_REPLIES("/api/comments/replies/{id}", HttpMethod.GET),

    COMMENT_LIST_REVIEW("/api/comments/review/{id}", HttpMethod.GET),
    COMMENT_CREATE_REVIEW("/api/comments/review/{id}", HttpMethod.POST),

    COMMENT_UPDATE("/api/comments/{id}", HttpMethod.PUT),
    COMMENT_DELETE("/api/comments/{id}", HttpMethod.DELETE),

    // ================= READING =================
    READING_ADD("/api/reading/add", HttpMethod.POST),
    READING_PROGRESS_UPDATE("/api/reading/update", HttpMethod.POST),

    // ================= NOTIFICATION =================
    NOTIFICATION_VIEW("/api/notifications", HttpMethod.GET),
    UNKNOWN("", null );

    private final HttpMethod method;

    private final Pattern compiledPattern;

    ActionType(String pathPattern, HttpMethod method) {
        this.method = method;
        if (pathPattern == null || pathPattern.isEmpty()) {
            this.compiledPattern = null;
        } else {
            // Replace {xxx} → [^/]+
            String regex = "^" + pathPattern
                    .replaceAll("\\{[^/]+}", "[^/]+") + "$";

            this.compiledPattern = Pattern.compile(regex);
        }
    }
}
