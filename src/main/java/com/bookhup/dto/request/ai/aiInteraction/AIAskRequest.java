package com.bookhup.dto.request.ai.aiInteraction;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AIAskRequest {

    private Long bookId;
    private String question;
    private AIContext context;
}


