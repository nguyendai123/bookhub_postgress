package com.bookhup.controller;

import com.bookhup.dto.request.behavior.TrackEventRequest;
import com.bookhup.model.ActionType;
import com.bookhup.model.UserBehaviorLog;
import com.bookhup.security.SecurityUtil;
import com.bookhup.service.queue.BehaviorLogQueue;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/track")
@RequiredArgsConstructor
public class TrackEventController {

    private final BehaviorLogQueue queue;

    @PostMapping("/event")
    public void trackEvent(@RequestBody TrackEventRequest request) {
        var user = SecurityUtil.getCurrentUser();
        ServletRequestAttributes attr =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attr == null) return;
        HttpServletRequest req = attr.getRequest();
        for (TrackEventRequest.Event event : request.getEvents()) {
            UserBehaviorLog log = UserBehaviorLog.builder()
                    .userId(user != null ? user.getUserId() : null)
                    .username(user != null ? user.getUsername() : null)
                    .actionType(resolveActionType(event))
                    .metadata(Map.of(
                            "targetType", event.getTargetType(),
                            "targetId", event.getTargetId(),
                            "eventType", event.getEventType(),
                            "extra", event.getExtra()
                    ))
                    .device(SecurityUtil.getDevice(req))
                    .location(SecurityUtil.getLocation(req))
                    .timestamp(LocalDateTime.now())
                    .build();

            queue.push(log);
        }
    }

    private ActionType resolveActionType(TrackEventRequest.Event event) {
        return switch (event.getTargetType()) {
            case "POST_IMAGE" -> ActionType.POST_CLICK_IMAGE;
            case "COMMENT_SECTION" -> ActionType.POST_CLICK_COMMENT_SECTION;
            case "POST_VIEW" -> ActionType.POST_VIEW;
            case "REVIEW_VIEW" -> ActionType.READING_ADD;
            default -> ActionType.UNKNOWN;
        };
    }
}
