package com.iuh.fit.service;

import com.iuh.fit.dto.response.FoodResponse;
import com.iuh.fit.entity.Food;
import com.iuh.fit.repository.FoodRepository;
import com.iuh.fit.repository.OrderRepository;
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
    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public List<FoodResponse> getPopularFoods(int limit) {
        return foodRepository.findTopByOrderCount(PageRequest.of(0, limit)).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<FoodResponse> getPersonalizedRecommendations(Long userId, int limit) {
        List<Long> orderedFoodIds = orderRepository.findFoodIdsByUserOrderHistory(userId);

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
