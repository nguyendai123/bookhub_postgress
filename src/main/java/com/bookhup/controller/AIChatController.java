package com.bookhup.controller;

import com.bookhup.dto.request.ai.aiInteraction.AIAskRequest;
import com.bookhup.dto.response.ai.aiInteraction.AIChatHistoryDTO;
import com.bookhup.model.AIInteraction;
import com.bookhup.model.User;
import com.bookhup.service.AIChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai/interaction")
@RequiredArgsConstructor
public class AIChatController {

    private final AIChatService service;

    @GetMapping
    public List<AIChatHistoryDTO> getChatHistory(
            @RequestParam Long bookId,
            @RequestAttribute("currentUser") User user
    ) {
        return service.getChatHistory(user.getUserId(), bookId);
    }

    @PostMapping
    public AIInteraction ask(
            @RequestAttribute("currentUser") User user,
            @RequestBody AIAskRequest req) {
        return service.ask(user, req);
    }
}

