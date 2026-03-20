package com.iuh.fit.controller;

import com.iuh.fit.dto.request.CreateFoodRequest;
import com.iuh.fit.dto.response.FoodResponse;
import com.iuh.fit.service.FoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/foods")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class FoodController {

    private final FoodService foodService;

    @GetMapping
    public ResponseEntity<List<FoodResponse>> getAvailableFoods() {
        return ResponseEntity.ok(foodService.getAllAvailableFoods());
    }

    @GetMapping("/all")
    public ResponseEntity<List<FoodResponse>> getAllFoods() {
        return ResponseEntity.ok(foodService.getAllFoods());
    }

    @PostMapping
    public ResponseEntity<FoodResponse> createFood(@RequestBody CreateFoodRequest request) {
        return ResponseEntity.ok(foodService.createFood(request));
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<FoodResponse> toggleAvailability(@PathVariable Long id) {
        return ResponseEntity.ok(foodService.toggleAvailability(id));
    }
}
