package com.bookhup.service.impl;

import com.bookhup.dto.request.ai.aiInteraction.AIAskRequest;
import com.bookhup.dto.request.ai.aiInteraction.AIContext;
import com.bookhup.dto.response.ai.aiInteraction.AIAnswerResponse;
import com.bookhup.dto.response.ai.summary.AISummaryAIResult;
import com.bookhup.model.AIInteraction;
import com.bookhup.service.AIClient;
import com.bookhup.service.GroqClient;
import com.bookhup.service.HuggingFaceClient;
import lombok.RequiredArgsConstructor;
import me.bush.translator.Language;
import me.bush.translator.Translator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class HuggingFaceClientImpl implements HuggingFaceClient {

//    private final RestTemplate restTemplate;
    private final RestTemplate hugRestTemplate;
    private final GroqClient groq;
    private final Translator translator = new Translator();

    @Value("${huggingface.token}")
    private String hfToken;
    private static final String SUMMARY_URL =
            "https://router.huggingface.co/hf-inference/models/facebook/bart-large-cnn";

    private static final String HF_API_URL = "https://router.huggingface.co/hf-inference/models/";

    @Override
    public String translate(String text, String sourceLang, String targetLang) {

        if (text == null || text.isBlank()) return text;
        if (sourceLang.equalsIgnoreCase(targetLang)) return text;

        List<String> chunks = splitText(text, 5000);
        List<String> translatedParts = new ArrayList<>();
        Language target = getLanguageFromCode(targetLang);

        for (String chunk : chunks) {
            try {
                // 1. Dịch đoạn nhỏ (chunk), không phải toàn bộ text
                var result = translator.translateBlocking(chunk, target);
                String translated = result.getTranslatedText();

                // 2. Thêm vào danh sách kết quả
                translatedParts.add(translated);

                // 3. Tạm dừng một chút (ví dụ 1 giây) để tránh bị Google quét Bot
                Thread.sleep(1000);

            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                return "Translation interrupted";
            } catch (Exception e) {
                // Nếu một đoạn lỗi, bạn có thể chọn bỏ qua hoặc ném lỗi
                System.err.println("Error at chunk: " + e.getMessage());
                translatedParts.add("[Translation Error]");
            }
        }

        // Gộp các phần đã dịch lại thành văn bản hoàn chỉnh
        return String.join(" ", translatedParts);

    }

    private Language getLanguageFromCode(String code) {
        return switch (code.toUpperCase()) {
            case "EN" -> Language.ENGLISH;
            case "VI" -> Language.VIETNAMESE;
            case "JA" -> Language.JAPANESE;
            case "KO" -> Language.KOREAN;
            case "FR" -> Language.FRENCH;
            // Thêm các ngôn ngữ khác bạn cần ở đây
            default -> Language.AUTO;
        };
    }

    private String getTranslationModel(String source, String target) {

        source = source.toLowerCase();
        target = target.toLowerCase();

        if (source.equals("en") && target.equals("vi"))
            return "Helsinki-NLP/opus-mt-en-vi";

        if (source.equals("vi") && target.equals("en"))
            return "Helsinki-NLP/opus-mt-vi-en";

        if (source.equals("en") && target.equals("fr"))
            return "Helsinki-NLP/opus-mt-en-fr";

        if (source.equals("en") && target.equals("ja"))
            return "Helsinki-NLP/opus-mt-en-jap";

        // fallback đa ngôn ngữ
        return "facebook/nllb-200-distilled-600M";
    }


    @Override
    public AISummaryAIResult summarize(
            String content,
            String bookTitle,
            String bookLang,
            String author,
            String scope,
            String lang
    ) {

        // 🌍 Nếu sách khong phai en → dịch sang tiếng Anh trước
        if (!"en".equalsIgnoreCase(bookLang)) {
            content = translate(content, bookLang, "en");
        }

        // 1. Split content into chunks (BART-safe)
        List<String> chunks = splitText(content, 1000);

        List<String> partialSummaries = new ArrayList<>();

        for (String chunk : chunks) {
            String chunkSummary = summarizeChunkWithRetry(chunk, bookTitle, author, scope);

            if (chunkSummary != null && !chunkSummary.isBlank()) {
                partialSummaries.add(chunkSummary);
            }
        }


        // 2. Merge partial summaries
        String mergedSummary = String.join("\n", partialSummaries);

        // 3. Final summarization pass
        String finalSummary = summarizeMergedRetry(
                mergedSummary,
                bookTitle,
                author,
                scope
        );

        var prompt = "vi".equalsIgnoreCase(lang) ? String.format("""
                Hãy viết lại đoạn tóm tắt sau theo văn phong trôi chảy, mạch lạc và giàu cảm xúc hơn, giống như phần giới thiệu nội dung sách trên bìa sau.
                Giữ nguyên các sự kiện quan trọng, không thêm tình tiết mới, nhưng có thể diễn đạt lại để câu văn tự nhiên, hấp dẫn và liền mạch hơn.
                Tránh liệt kê rời rạc, hãy liên kết các ý thành một câu chuyện ngắn gọn, dễ đọc.
                                     
                Đoạn gốc: 
                %s""", finalSummary) :
                String.format("""
                Rewrite the following summary in a smooth, emotionally rich narrative style, similar to a professional book introduction.
                Keep the main events, but improve coherence, connection, and narrative quality.
                Avoid disjointed sentences or lists – connect ideas naturally into a concise, engaging paragraph.
                Use a tone appropriate for a novel, especially one aimed at young adults if the story contains magical or fantasy elements. Write the summary in English.
                
                Original text:
                        %s""", finalSummary);
        AIContext context = new AIContext(
                bookTitle,
                Collections.singletonList(""),
                Collections.singletonList("")
        );
        var req = new AIAskRequest(
                0L,
                prompt,
                context
        );
        AIAnswerResponse ai = groq.ask(req);

        return AISummaryAIResult.builder()
                .summary(normalizeText(ai.getAnswer()))
                .confidence(0.9f)
                .modelVersion("facebook/bart-large-cnn")
                .build();
    }

    private String normalizeText(String text) {
        if (text == null) return null;

        return text
                .replace("\\\"", "\"")   // bỏ escape của ngoặc kép
                .replace("\\'", "'")     // bỏ escape của nháy đơn
                .replace("\\\"", "")
                .replace("‘", "'")
                .replace("’", "'")
                .replace("�", "")
                .replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "")
                .replaceAll("\\s{2,}", " ")
                .trim();
    }

    private String summarizeChunk(
            String chunk,
            String bookTitle,
            String author,
            String scope
    ) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(hfToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        Map<String, Object> body = Map.of(
                "inputs", chunk,
                "parameters", Map.of(
                        "max_length", 250,
                        "do_sample", false
                )
        );

        HttpEntity<?> req = new HttpEntity<>(body, headers);

        ResponseEntity<List<Map<String, Object>>> res =
                hugRestTemplate.exchange(
                        SUMMARY_URL,
                        HttpMethod.POST,
                        req,
                        new ParameterizedTypeReference<>() {}
                );

        return (String) res.getBody().get(0).get("summary_text");
    }

    private String summarizeChunkWithRetry(
            String chunk,
            String bookTitle,
            String author,
            String scope
    ) {

        int maxRetries = 3;
        int attempt = 0;

        while (attempt < maxRetries) {
            try {
                return summarizeChunk(chunk, bookTitle, author, scope);
            } catch (Exception ex) {
                attempt++;
                System.err.println("⚠️ Summarize chunk failed (attempt " + attempt + "): " + ex.getMessage());

                // Nếu là lỗi client 4xx → thường do input quá dài hoặc sai format → không retry nữa
                if (ex instanceof org.springframework.web.client.HttpClientErrorException) {
                    System.err.println("❌ Client error, skipping this chunk...");
                    break;
                }

                // Chờ 1 chút rồi retry (tránh spam API)
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {}
            }
        }

        System.err.println("🚫 Skipping failed chunk after retries");
        return null; // trả null để biết chunk này fail
    }

    private String summarizeMergedRetry(
            String merged,
            String bookTitle,
            String author,
            String scope
    ) {
        int maxRetries = 3;
        long delayMs = 2000; // đợi 2s giữa các lần retry

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                return summarizeMerged(merged, bookTitle, author, scope);

            } catch (ResourceAccessException e) {
                // Lỗi timeout / network
                System.out.println("⏱ HF timeout (attempt " + attempt + "): " + e.getMessage());

            } catch (HttpClientErrorException.TooManyRequests e) {
                // Bị rate limit
                System.out.println("🚦 HF rate limited (attempt " + attempt + ")");

            } catch (HttpServerErrorException e) {
                // Lỗi phía HuggingFace server
                System.out.println("💥 HF server error (attempt " + attempt + "): " + e.getStatusCode());

            } catch (Exception e) {
                // Lỗi khác
                System.out.println("❌ Unexpected HF error (attempt " + attempt + "): " + e.getMessage());
            }

            // Nếu chưa phải lần cuối thì chờ rồi thử lại
            if (attempt < maxRetries) {
                try {
                    Thread.sleep(delayMs * attempt); // backoff tăng dần
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        // 🔻 Nếu tất cả retry thất bại → fallback
        System.out.println("⚠ All HF retries failed → using fallback merged summary");

        // Cắt bớt nếu quá dài để tránh lưu DB quá nặng
        return merged.length() > 2000
                ? merged.substring(0, 2000)
                : merged;
    }

    private String summarizeMerged(
            String merged,
            String bookTitle,
            String author,
            String scope
    ) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(hfToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        String prompt = String.format(
                """
                        Please write a clear and neutral summary in English %s summary,
                        similar to professional book summaries found on the internet.
                            
                         Book:
                         - Title: %s
                         - Author: %s
                            
                         Summaries:
                         %s
                         """,
                scope.equals("CHAPTER") ? "chapter" : "book",
                bookTitle,
                author,
                merged
        );

        Map<String, Object> body = Map.of(
                "inputs", prompt,
                "parameters", Map.of(
                        "max_length", scope.equals("BOOK") ? 1200 : 500,
                        "min_length", scope.equals("BOOK") ? 600 : 250,
                        "do_sample", false
                )
        );

        HttpEntity<?> req = new HttpEntity<>(body, headers);

        ResponseEntity<List<Map<String, Object>>> res =
                hugRestTemplate.exchange(
                        SUMMARY_URL,
                        HttpMethod.POST,
                        req,
                        new ParameterizedTypeReference<>() {
                        }
                );

        return (String) res.getBody().get(0).get("summary_text");
    }

    private List<String> splitText(String text, int maxChars) {
        List<String> chunks = new ArrayList<>();
        int start = 0;

        while (start < text.length()) {
            int end = Math.min(start + maxChars, text.length());
            chunks.add(text.substring(start, end));
            start = end;
        }
        return chunks;
    }

}
