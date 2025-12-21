package com.bookhup.service;

import com.bookhup.dto.response.ai.summary.AISummaryAIResult;

public interface AIClient {
    AISummaryAIResult summarize(String content, String lang);
}

