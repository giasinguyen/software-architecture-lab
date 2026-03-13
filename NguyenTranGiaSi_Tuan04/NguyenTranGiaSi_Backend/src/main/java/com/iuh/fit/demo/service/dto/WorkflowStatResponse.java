package com.iuh.fit.demo.service.dto;

public record WorkflowStatResponse(
        String contentType,
        long draftCount,
        long reviewCount,
        long approvedCount,
        long publishedCount
) {
}
