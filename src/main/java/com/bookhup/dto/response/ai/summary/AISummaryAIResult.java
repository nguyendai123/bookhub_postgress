package com.bookhup.dto.response.ai.summary;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AISummaryAIResult {

    /**
     * Nội dung tóm tắt sinh bởi AI
     */
    private String summary;

    /**
     * Từ khóa quan trọng (đã extract bởi AI)
     * VD: ["trưởng thành", "xung đột", "gia đình"]
     */
    private List<String> keywords;

    /**
     * Chủ đề chính của nội dung
     * VD: ["Tâm lý học", "Phát triển nhân vật"]
     */
    private List<String> topics;

    /**
     * Độ tin cậy AI tự đánh giá (0.0 → 1.0)
     */
    private Float confidence;

    /**
     * Version model AI
     * VD: gpt-4.1, llama-3-70b, bookhub-ai-v2
     */
    private String modelVersion;

    /**
     * Embedding vector (optional – dùng cho search/recommend)
     * Có thể null nếu chưa cần
     */
    private byte[] embeddingVector;

    /** ===== ERROR INFO ===== */
    private Boolean success;
    private String errorCode;
    private String errorMessage;

    /* ================= FACTORY METHODS ================= */

    public static AISummaryAIResult success(String summary) {
        return AISummaryAIResult.builder()
                .success(true)
                .summary(summary)
                .confidence(0.8f)
                .modelVersion("gpt-4.1")
                .build();
    }

    public static AISummaryAIResult error(String errorCode, String errorMessage) {
        return AISummaryAIResult.builder()
                .success(false)
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .confidence(0.0f)
                .build();
    }
}

