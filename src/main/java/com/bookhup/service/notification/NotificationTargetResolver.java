package com.bookhup.service.notification;

import com.bookhup.cache.OwnerCache;
import com.bookhup.model.ActionType;
import com.bookhup.model.TargetResult;
import com.bookhup.model.TargetType;
import com.bookhup.repository.BookReviewRepository;
import com.bookhup.repository.CommentRepository;
import com.bookhup.repository.FollowRepository;
import com.bookhup.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

//@Component
//public class TargetUserResolver {
//
//    public Long resolve(ActionType action, String uri, Map<String, Object> metadata, Long currentUserId) {
//
//        // 1. CÁC ACTION TỰ TARGET VÀO USER HIỆN TẠI
//        if (isSelfAction(action)) {
//            return currentUserId;
//        }
//
//        // 2. LẤY TARGET TỪ PATH (VD: /api/follow/{id})
//        Long fromPath = extractIdFromUri(uri);
//        if (fromPath != null) return fromPath;
//
//        // 3. LẤY targetUserId TỪ METADATA (DTO hoặc param)
//        Object meta = metadata.get("targetUserId");
//        if (meta != null) {
//            return Long.parseLong(meta.toString());
//        }
//
//        // 4. Không xác định được
//        return 0L;
//    }
//
//    private boolean isSelfAction(ActionType action) {
//        return switch (action) {
//            case AUTH_LOGIN, AUTH_LOGOUT, AUTH_REGISTER,
//                 AUTH_RESET_PASSWORD, USER_UPDATE_PROFILE,
//                 NOTIFICATION_VIEW -> true;
//            default -> false;
//        };
//    }
//
//    /**
//     * Ví dụ /api/follow/5 → return 5
//     */
//    private Long extractIdFromUri(String uri) {
//        String[] parts = uri.split("/");
//        try {
//            return Long.parseLong(parts[parts.length - 1]);
//        } catch (Exception e) {
//            return null;
//        }
//    }
//}

@Component
@RequiredArgsConstructor
public class NotificationTargetResolver {

    private final FollowRepository followRepo;
    private final PostRepository postRepo;
    private final CommentRepository commentRepo;
    private final BookReviewRepository reviewRepo;

    public TargetResult resolve(ActionType action,
                                Map<String, Object> metadata,
                                Long currentUserId) {

        /* ================= NHÓM A – SELF / SYSTEM ================= */
        if (isGroupA(action)) {
            return TargetResult.self(currentUserId);
        }

        /* ================= NHÓM B – FOLLOW GRAPH ================= */
        if (isGroupB(action)) {
            Set<Long> followers = followRepo.findFollowerIds(currentUserId);
            return followers.isEmpty()
                    ? TargetResult.none()
                    : TargetResult.multi(followers);
        }

        /* ================= NHÓM C – ENTITY OWNER ================= */
        return resolveGroupC(action, metadata);

        /* ================= NHÓM D – DOMAIN EVENT ================= */
        // NHÓM D KHÔNG ĐI QUA AUTO LOG
    }

    private boolean isGroupA(ActionType action) {
        return switch (action) {
            case AUTH_REGISTER,
                 AUTH_LOGIN,
                 AUTH_RESET_PASSWORD,
                 USER_UPDATE_PROFILE,
                 NOTIFICATION_VIEW -> true;
            default -> false;
        };
    }

    private boolean isGroupB(ActionType action) {
        return switch (action) {
            case USER_FOLLOW,
                 POST_CREATE,
                 REVIEW_CREATE,
                 READING_ADD,
                 READING_PROGRESS_UPDATE -> true;
            default -> false;
        };
    }

    private TargetResult resolveGroupC(ActionType action,
                                       Map<String, Object> metadata) {

        return switch (action) {

            /* ================= POST ================= */
            case COMMENT_CREATE_POST,
                 POST_LIKE,
                 POST_SHARE -> {

                Long postId = action == ActionType.POST_LIKE
                        ? getLong((Map<String, Object>) metadata.get("req"), "targetId")
                        : getLong(metadata, "postId");

                if (postId == null) yield TargetResult.none();

                // 1️⃣ CACHE FIRST
                Long ownerId = OwnerCache.POST_OWNER_CACHE.getIfPresent(postId);
                if (ownerId != null) {
                    yield TargetResult.single(ownerId);
                }

                // 2️⃣ FALLBACK DB
                yield postRepo.findOwnerId(postId)
                        .map(id -> {
                            OwnerCache.POST_OWNER_CACHE.put(postId, id);
                            return TargetResult.single(id);
                        })
                        .orElse(TargetResult.none());
            }

            /* ================= COMMENT ================= */
            case COMMENT_LIKE -> {

                Long commentId = getLong(metadata, "commentId");
                if (commentId == null) yield TargetResult.none();

                Long ownerId = OwnerCache.COMMENT_OWNER_CACHE.getIfPresent(commentId);
                if (ownerId != null) {
                    yield TargetResult.single(ownerId);
                }

                yield commentRepo.findOwnerId(commentId)
                        .map(id -> {
                            OwnerCache.COMMENT_OWNER_CACHE.put(commentId, id);
                            return TargetResult.single(id);
                        })
                        .orElse(TargetResult.none());
            }

            /* ================= REVIEW ================= */
            case BOOKREVIEW_LIKE -> {

                Long reviewId = getLong(metadata, "reviewId");
                if (reviewId == null) yield TargetResult.none();

                Long ownerId = OwnerCache.REVIEW_OWNER_CACHE.getIfPresent(reviewId);
                if (ownerId != null) {
                    yield TargetResult.single(ownerId);
                }

                yield reviewRepo.findOwnerId(reviewId)
                        .map(id -> {
                            OwnerCache.REVIEW_OWNER_CACHE.put(reviewId, id);
                            return TargetResult.single(id);
                        })
                        .orElse(TargetResult.none());
            }

            /* ================= MENTION ================= */
            case POST_CREATE, REVIEW_CREATE -> {
                @SuppressWarnings("unchecked")
                Set<Long> mentioned = (Set<Long>) metadata.get("mentionedUserIds");
                yield mentioned == null || mentioned.isEmpty()
                        ? TargetResult.none()
                        : TargetResult.multi(mentioned);
            }

            default -> TargetResult.none();
        };
    }




    private Long getLong(Map<String, Object> metadata, String key) {

        if (metadata == null || key == null) {
            return null;
        }

        Object value = metadata.get(key);
        if (value == null) {
            return null;
        }

        // Trường hợp đã là Number
        if (value instanceof Number number) {
            return number.longValue();
        }

        // Trường hợp là String
        if (value instanceof String str) {
            try {
                return Long.parseLong(str);
            } catch (NumberFormatException e) {
                return null;
            }
        }

        // Trường hợp là Map (DTO bị serialize)
        if (value instanceof Map<?, ?> map) {
            Object id = map.get("id");
            if (id instanceof Number number) {
                return number.longValue();
            }
            if (id instanceof String str) {
                try {
                    return Long.parseLong(str);
                } catch (Exception ignored) {}
            }
        }

        return null;
    }


}


