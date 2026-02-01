package com.bookhup.service;

import com.bookhup.dto.request.ai.aiInteraction.AIAskRequest;
import com.bookhup.dto.response.ai.aiInteraction.AIAnswerResponse;
import com.bookhup.dto.response.ai.highLight.AIHighlightResponse;
import com.bookhup.dto.response.ai.summary.AISummaryAIResult;

import java.util.List;

public interface AIClient {
    String translate(String text, String sourceLang, String targetLang);
    // CHAPTER | BOOK
    AISummaryAIResult summarize(String content, String bookTitle, String bookLang, String author, String scope, String lang);

    AIHighlightResponse highlight(String text);

    AIAnswerResponse ask(AIAskRequest req, Long bookId);

    List<String> generateHighlights(String textContent);
}
