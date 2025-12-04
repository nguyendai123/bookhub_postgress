package com.bookhup.dto.request.behavior;

import lombok.Data;

import java.util.List;

@Data
public class TrackEventRequest {
    private List<Event> events;

    @Data
    public static class Event {
        private String targetType; // POST_IMAGE, COMMENT_SECTION, POST, COMMENT, BOOKREVIEW...
        private Long targetId;
        private String eventType;  // CLICK, VIEW, LIKE...
        private String extra;      // Optional: JSON string hoặc info bổ sung
    }
}

