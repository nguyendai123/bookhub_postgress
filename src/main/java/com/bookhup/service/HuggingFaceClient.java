package com.bookhup.service;

import com.bookhup.dto.response.ai.summary.AISummaryAIResult;

public interface HuggingFaceClient {

    String translate(String text, String sourceLang, String targetLang);

    AISummaryAIResult summarize(String content, String bookTitle,String bookLang, String author, String scope, String lang);
}
