package com.iuh.fit.demo.service.dto;

public record EventFlowStatResponse(
        String eventName,
        String producerPlugin,
        String consumerPlugin,
        double successRate,
        long avgLatencyMs
) {
}
