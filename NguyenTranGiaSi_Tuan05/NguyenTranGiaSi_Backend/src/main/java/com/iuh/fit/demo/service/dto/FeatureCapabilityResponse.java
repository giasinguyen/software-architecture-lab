package com.iuh.fit.demo.service.dto;

public record FeatureCapabilityResponse(
        String featureId,
        String pluginId,
        String category,
        int endpointCount,
        int eventCount,
        boolean active
) {
}
