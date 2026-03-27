package com.demo.orderservice.client;

import com.demo.orderservice.client.dto.FoodDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class FoodServiceClient {

    private final RestTemplate restTemplate;

    @Value("${services.food-url}")
    private String foodServiceUrl;

    public FoodDto getFoodById(Long foodId) {
        String url = foodServiceUrl + "/api/foods/" + foodId;
        return restTemplate.getForObject(url, FoodDto.class);
    }

    /** Gọi để tăng orderCount sau khi order được đặt thành công */
    public void incrementOrderCount(Long foodId, int qty) {
        try {
            String url = foodServiceUrl + "/api/foods/" + foodId + "/order-count?qty=" + qty;
            restTemplate.patchForObject(url, null, Void.class);
        } catch (Exception ignored) {
            // best-effort: không làm fail order nếu update orderCount lỗi
        }
    }
}
