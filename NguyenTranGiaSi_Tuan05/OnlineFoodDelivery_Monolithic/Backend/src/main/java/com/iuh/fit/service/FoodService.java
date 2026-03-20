package com.iuh.fit.service;

import com.iuh.fit.dto.request.CreateFoodRequest;
import com.iuh.fit.dto.response.FoodResponse;
import com.iuh.fit.entity.Food;
import com.iuh.fit.repository.FoodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FoodService {

    private final FoodRepository foodRepository;

    @Transactional(readOnly = true)
    public List<FoodResponse> getAllAvailableFoods() {
        return foodRepository.findByAvailableTrue().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<FoodResponse> getAllFoods() {
        return foodRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public FoodResponse createFood(CreateFoodRequest request) {
        Food food = Food.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .category(request.getCategory())
                .imageUrl(request.getImageUrl())
                .available(true)
                .orderCount(0)
                .build();
        return toResponse(foodRepository.save(food));
    }

    @Transactional
    public FoodResponse toggleAvailability(Long foodId) {
        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy món ăn: " + foodId));
        food.setAvailable(!food.isAvailable());
        return toResponse(foodRepository.save(food));
    }

    public FoodResponse toResponse(Food food) {
        return FoodResponse.builder()
                .id(food.getId())
                .name(food.getName())
                .description(food.getDescription())
                .price(food.getPrice())
                .category(food.getCategory())
                .imageUrl(food.getImageUrl())
                .available(food.isAvailable())
                .orderCount(food.getOrderCount())
                .build();
    }
}
