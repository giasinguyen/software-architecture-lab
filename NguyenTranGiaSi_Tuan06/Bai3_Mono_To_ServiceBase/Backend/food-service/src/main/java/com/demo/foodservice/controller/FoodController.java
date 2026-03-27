package com.demo.foodservice.controller;

import com.demo.foodservice.dto.CreateFoodRequest;
import com.demo.foodservice.dto.FoodResponse;
import com.demo.foodservice.service.FoodService;
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

    @GetMapping("/{id}")
    public ResponseEntity<FoodResponse> getFoodById(@PathVariable Long id) {
        return ResponseEntity.ok(foodService.getFoodById(id));
    }

    @PostMapping
    public ResponseEntity<FoodResponse> createFood(@RequestBody CreateFoodRequest request) {
        return ResponseEntity.ok(foodService.createFood(request));
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<FoodResponse> toggleAvailability(@PathVariable Long id) {
        return ResponseEntity.ok(foodService.toggleAvailability(id));
    }

    // order-service gọi sau khi đặt hàng thành công
    @PatchMapping("/{id}/order-count")
    public ResponseEntity<Void> incrementOrderCount(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") int qty) {
        foodService.incrementOrderCount(id, qty);
        return ResponseEntity.ok().build();
    }
}
