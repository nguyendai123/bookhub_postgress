package com.bookhup.dto.response.ai.aiInteraction;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AIChatHistoryDTO {

    private Long interactionId;
    private String question;
    private String answer;
    private LocalDateTime createdAt;
}
