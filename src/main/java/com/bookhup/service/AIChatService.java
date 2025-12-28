package com.bookhup.service;

import com.bookhup.dto.request.ai.aiInteraction.AIAskRequest;
import com.bookhup.model.AIInteraction;
import com.bookhup.model.User;
import org.springframework.transaction.annotation.Transactional;

public interface AIChatService {
    @Transactional
    AIInteraction ask(User user, AIAskRequest req);
}
