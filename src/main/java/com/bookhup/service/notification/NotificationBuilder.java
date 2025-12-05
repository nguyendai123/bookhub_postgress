package com.bookhup.service.notification;

import com.bookhup.model.Notification;
import com.bookhup.model.NotificationType;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

@Component
public class NotificationBuilder {

    public Notification build(NotificationType type, Long userId, Map<String, Object> data) {
        String content = switch (type) {
            case N011_COMMENT_ON_POST -> data.get("actor") + " đã bình luận bài viết của bạn.";
            case N012_REPLY_COMMENT ->
                    data.get("actor") + " đã trả lời bình luận của bạn về bài viết " + data.get("postContent");
            case N015_LIKE_COMMENT -> data.get("actor") + " đã thích bình luận của bạn.";
            case N050_FOLLOW_YOU -> data.get("actor") + " đã bắt đầu theo dõi bạn.";
            default -> "Bạn có thông báo mới.";
        };

        return Notification.builder()
                .type(type)
                .userId(userId)
                .priority(NotificationPriorityResolver.priorityOf(type))
                .content(content)
                .metadata((JsonNode) data)
                .createdAt(LocalDateTime.now())
                .read(false)
                .build();
    }
}

