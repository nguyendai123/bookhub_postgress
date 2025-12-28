package com.bookhup.controller;

import com.bookhup.dto.response.ai.recommendation.RecommendationDTO;
import com.bookhup.model.User;
import com.bookhup.service.impl.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ai/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService service;

    @GetMapping
    public List<RecommendationDTO> recommend(@RequestAttribute("currentUser") User user) {
        return service.recommend(user);
    }
}

