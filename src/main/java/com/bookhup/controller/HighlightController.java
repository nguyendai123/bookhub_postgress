package com.bookhup.controller;

import com.bookhup.dto.request.ai.highLight.HighlightRequest;
import com.bookhup.model.BookHighlight;
import com.bookhup.model.User;
import com.bookhup.service.HighlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai/highlight")
@RequiredArgsConstructor
public class HighlightController {

    private final HighlightService service;

    @PostMapping
    public BookHighlight highlight(
            @RequestAttribute("currentUser") User user,
            @RequestBody HighlightRequest req) {
        return service.highlight(user, req);
    }

    @GetMapping("/{chapterId}")
    public List<BookHighlight> getHighlights(@RequestAttribute("currentUser") User user,
                                             @PathVariable Long chapterId) {
        return service.getHighlights(chapterId, user);
    }
}

