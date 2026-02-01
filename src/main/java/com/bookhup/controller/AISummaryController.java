package com.bookhup.controller;

import com.bookhup.dto.request.ai.summary.AISummaryRequest;
import com.bookhup.dto.response.ai.summary.AISummaryResponse;
import com.bookhup.model.User;
import com.bookhup.service.AISummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AISummaryController {

    private final AISummaryService aiSummaryService;

    @PostMapping("/summary")
    public ResponseEntity<AISummaryResponse> summarize(
            @RequestBody AISummaryRequest request,
            @RequestAttribute("currentUser") User user
    ) {
        return ResponseEntity.ok(
                aiSummaryService.generateSummary(request, user)
        );
    }

    // 🔹 API lấy summary theo ngôn ngữ
    @GetMapping("/summary")
    public ResponseEntity<AISummaryResponse> getSummary(
            @RequestParam Long bookId,
            @RequestParam(required = false) Long chapterId,
            @RequestParam(defaultValue = "en") String lang
    ) {
        return ResponseEntity.ok(
                aiSummaryService.getSummary(bookId, chapterId, lang)
        );
    }

}

