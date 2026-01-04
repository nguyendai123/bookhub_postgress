package com.bookhup.service.impl;

import com.bookhup.dto.request.ai.aiInteraction.AIAskRequest;
import com.bookhup.dto.response.ai.aiInteraction.AIAnswerResponse;
import com.bookhup.dto.response.ai.highLight.AIHighlightResponse;
import com.bookhup.dto.response.ai.summary.AISummaryAIResult;
import com.bookhup.service.AIClient;
import com.bookhup.service.GroqClient;
import com.bookhup.service.HuggingFaceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AIClientImpl implements AIClient {


    private final HuggingFaceClient hf;
    private final GroqClient groq;

    @Override
    public AISummaryAIResult summarize(String content, String bookTitle, String author, String scope, String lang) {
        return hf.summarize(content, bookTitle, author, scope, lang);
    }

    @Override
    public AIHighlightResponse highlight(String text) {
        return groq.highlight(text);
    }

    @Override
    public AIAnswerResponse ask(AIAskRequest req, Long bookId) {
        return groq.ask(req);
    }

    @Override
    public List<String> generateHighlights(String textContent) {
        return groq.generateHighlights(textContent);
    }

}
