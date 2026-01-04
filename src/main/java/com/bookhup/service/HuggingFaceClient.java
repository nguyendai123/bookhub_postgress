package com.bookhup.service;

import com.bookhup.dto.response.ai.summary.AISummaryAIResult;

public interface HuggingFaceClient {

    AISummaryAIResult summarize(String content, String bookTitle, String author, String scope, String lang);
}
