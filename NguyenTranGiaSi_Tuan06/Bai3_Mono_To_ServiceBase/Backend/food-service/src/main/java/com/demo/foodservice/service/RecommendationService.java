package com.demo.foodservice.service;

import com.demo.foodservice.dto.FoodResponse;
import com.demo.foodservice.entity.Food;
import com.demo.foodservice.repository.FoodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final FoodRepository foodRepository;

    @Transactional(readOnly = true)
    public List<FoodResponse> getPopularFoods(int limit) {
        return foodRepository.findTopByOrderCount(PageRequest.of(0, limit)).stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Service-based: query trực tiếp shared DB để lấy order history của user.
     * Không cần gọi HTTP sang order-service vì dùng chung database.
     */
    @Transactional(readOnly = true)
    public List<FoodResponse> getPersonalizedRecommendations(Long userId, int limit) {
        List<Long> orderedFoodIds = foodRepository.findFoodIdsByUserOrderHistory(userId);

        List<Food> personalizedFoods = orderedFoodIds.stream()
                .limit(limit)
                .map(id -> foodRepository.findById(id).orElse(null))
                .filter(f -> f != null && f.isAvailable())
                .toList();

        if (personalizedFoods.size() < limit) {
            List<Food> popular = foodRepository.findTopByOrderCount(PageRequest.of(0, limit));
            Map<Long, Food> combined = new LinkedHashMap<>();
            personalizedFoods.forEach(f -> combined.put(f.getId(), f));
            popular.forEach(f -> combined.put(f.getId(), f));
            return combined.values().stream().limit(limit).map(this::toResponse).toList();
        }

        return personalizedFoods.stream().map(this::toResponse).toList();
    }

    private FoodResponse toResponse(Food food) {
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
