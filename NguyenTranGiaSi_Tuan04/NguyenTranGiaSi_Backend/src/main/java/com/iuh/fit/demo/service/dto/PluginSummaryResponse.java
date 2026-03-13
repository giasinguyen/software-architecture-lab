package com.iuh.fit.demo.service.dto;

import com.iuh.fit.demo.domain.plugin.PluginStatus;

import java.time.LocalDateTime;

public record PluginSummaryResponse(
        String pluginId,
        String version,
        PluginStatus status,
        String owner,
        String description,
        LocalDateTime lastActivatedAt,
        LocalDateTime lastDeactivatedAt
) {
}
