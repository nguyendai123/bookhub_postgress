//package com.bookhup.service;
//
//import com.bookhup.dto.request.ai.aiInteraction.AIAskRequest;
//import com.bookhup.dto.request.ai.recommendation.AIRecommendationRequest;
//import com.bookhup.dto.response.ai.aiInteraction.AIAnswerResponse;
//import com.bookhup.dto.response.ai.highLight.AIHighlightResponse;
//import com.bookhup.dto.response.ai.recommendation.AIRecommendationResponse;
//import com.bookhup.dto.response.ai.summary.AISummaryAIResult;
//
//public interface AIClient {
//    AISummaryAIResult summarize(String content, String lang);
//
//    AIRecommendationResponse recommend(AIRecommendationRequest req);
//
//    AIHighlightResponse highlight(String text);
//
//    AIAnswerResponse ask(AIAskRequest question, Long bookId);
//}
//
