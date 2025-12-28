package com.bookhup.dto.response.ai.highLight;

import lombok.Data;

import java.util.List;

@Data
public class AIHighlightResponse {
    private String sentiment;
    private String summary;
    private List<String> keywords;
}

