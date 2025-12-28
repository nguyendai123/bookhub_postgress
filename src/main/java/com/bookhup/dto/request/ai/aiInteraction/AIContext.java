package com.bookhup.dto.request.ai.aiInteraction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AIContext {

    private String bookTitle;
    private List<String> summaries;
    private List<String> highlights;
}

