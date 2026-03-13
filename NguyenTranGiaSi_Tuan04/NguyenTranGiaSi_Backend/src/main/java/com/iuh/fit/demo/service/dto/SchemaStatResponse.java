package com.iuh.fit.demo.service.dto;

public record SchemaStatResponse(
        long contentTypeCount,
        long dynamicFieldCount,
        double validationErrorRate
) {
}
