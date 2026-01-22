package com.bookhup.service;

import com.bookhup.dto.request.ai.aiInteraction.AIAskRequest;
import com.bookhup.dto.response.ai.aiInteraction.AIChatHistoryDTO;
import com.bookhup.model.AIInteraction;
import com.bookhup.model.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AIChatService {
    List<AIChatHistoryDTO> getChatHistory(Long userId, Long bookId);

    @Transactional
    AIInteraction ask(User user, AIAskRequest req);
}
