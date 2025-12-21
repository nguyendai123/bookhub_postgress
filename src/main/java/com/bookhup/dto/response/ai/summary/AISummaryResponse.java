package com.bookhup.dto.response.ai.summary;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AISummaryResponse {
    private Long summaryId;
    private String summaryText;
    private List<String> keywords;
    private List<String> topics;
    private String modelVersion;
    private Float confidence;
}

