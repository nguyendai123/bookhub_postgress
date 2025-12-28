package com.bookhup.service;

import com.bookhup.dto.request.ai.summary.AISummaryRequest;
import com.bookhup.dto.response.ai.summary.AISummaryResponse;
import com.bookhup.model.User;

public interface AISummaryService {
    AISummaryResponse generateSummary(AISummaryRequest request, User user);
}
