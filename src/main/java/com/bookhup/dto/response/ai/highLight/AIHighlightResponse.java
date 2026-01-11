package com.bookhup.dto.response.ai.highLight;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AIHighlightResponse {
    private String sentiment;
    private String summary;
    private List<String> keywords;
}

