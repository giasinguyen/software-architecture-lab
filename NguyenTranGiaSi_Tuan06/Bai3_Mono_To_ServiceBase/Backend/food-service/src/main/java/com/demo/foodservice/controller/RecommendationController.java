package com.demo.foodservice.controller;

import com.demo.foodservice.dto.FoodResponse;
import com.demo.foodservice.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping("/popular")
    public ResponseEntity<List<FoodResponse>> getPopular(
            @RequestParam(defaultValue = "6") int limit) {
        return ResponseEntity.ok(recommendationService.getPopularFoods(limit));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<FoodResponse>> getPersonalized(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "6") int limit) {
        return ResponseEntity.ok(recommendationService.getPersonalizedRecommendations(userId, limit));
    }
}
