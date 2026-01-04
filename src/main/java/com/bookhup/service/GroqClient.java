package com.bookhup.service;

import com.bookhup.dto.request.ai.aiInteraction.AIAskRequest;
import com.bookhup.dto.response.ai.aiInteraction.AIAnswerResponse;
import com.bookhup.dto.response.ai.highLight.AIHighlightResponse;

import java.util.List;

public interface GroqClient {
    AIHighlightResponse highlight(String text);

    AIAnswerResponse ask(AIAskRequest req);

    List<String> generateHighlights(String textContent);
}
