package com.bookhup.service.notification;

import com.bookhup.model.ActionType;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TargetUserResolver {

    public Long resolve(ActionType action, String uri, Map<String, Object> metadata, Long currentUserId) {

        // 1. CÁC ACTION TỰ TARGET VÀO USER HIỆN TẠI
        if (isSelfAction(action)) {
            return currentUserId;
        }

        // 2. LẤY TARGET TỪ PATH (VD: /api/follow/{id})
        Long fromPath = extractIdFromUri(uri);
        if (fromPath != null) return fromPath;

        // 3. LẤY targetUserId TỪ METADATA (DTO hoặc param)
        Object meta = metadata.get("targetUserId");
        if (meta != null) {
            return Long.parseLong(meta.toString());
        }

        // 4. Không xác định được
        return 0L;
    }

    private boolean isSelfAction(ActionType action) {
        return switch (action) {
            case AUTH_LOGIN, AUTH_LOGOUT, AUTH_REGISTER,
                 AUTH_RESET_PASSWORD, USER_UPDATE_PROFILE,
                 NOTIFICATION_VIEW -> true;
            default -> false;
        };
    }

    /**
     * Ví dụ /api/follow/5 → return 5
     */
    private Long extractIdFromUri(String uri) {
        String[] parts = uri.split("/");
        try {
            return Long.parseLong(parts[parts.length - 1]);
        } catch (Exception e) {
            return null;
        }
    }
}

