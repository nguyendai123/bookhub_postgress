package com.bookhup.dto.response.ai.highLight;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AIHighlightResponse {
    private String sentiment;
    private String summary;
    private List<String> keywords;
}

