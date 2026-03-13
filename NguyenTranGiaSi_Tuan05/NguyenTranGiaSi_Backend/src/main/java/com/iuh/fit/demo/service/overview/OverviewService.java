package com.iuh.fit.demo.service.overview;

import com.iuh.fit.demo.service.dto.EventFlowStatResponse;
import com.iuh.fit.demo.service.dto.FeatureCapabilityResponse;
import com.iuh.fit.demo.service.dto.PluginSummaryResponse;
import com.iuh.fit.demo.service.dto.SchemaStatResponse;
import com.iuh.fit.demo.service.dto.WorkflowStatResponse;
import com.iuh.fit.demo.service.microkernel.EventBusMetricsService;
import com.iuh.fit.demo.service.microkernel.PluginRegistryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OverviewService {

    private final PluginRegistryService pluginRegistryService;
    private final EventBusMetricsService eventBusMetricsService;

    public OverviewService(PluginRegistryService pluginRegistryService, EventBusMetricsService eventBusMetricsService) {
        this.pluginRegistryService = pluginRegistryService;
        this.eventBusMetricsService = eventBusMetricsService;
    }

    public List<PluginSummaryResponse> getPluginOverview() {
        return pluginRegistryService.getPlugins();
    }

    public List<FeatureCapabilityResponse> getFeatureOverview() {
        return List.of(
                new FeatureCapabilityResponse("editor-rich-text", "content-editor", "content", 4, 2,
                        pluginRegistryService.isPluginActive("content-editor")),
                new FeatureCapabilityResponse("workflow-approval", "workflow-engine", "workflow", 3, 3,
                        pluginRegistryService.isPluginActive("workflow-engine")),
                new FeatureCapabilityResponse("schema-runtime", "dynamic-schema", "schema", 5, 1,
                        pluginRegistryService.isPluginActive("dynamic-schema"))
        );
    }

    public List<EventFlowStatResponse> getEventOverview() {
        return eventBusMetricsService.getEventFlowStats();
    }

    public List<WorkflowStatResponse> getWorkflowOverview() {
        return List.of(
                new WorkflowStatResponse("LandingPage", 12, 5, 3, 20),
                new WorkflowStatResponse("BlogPost", 18, 8, 6, 45)
        );
    }

    public SchemaStatResponse getSchemaOverview() {
        return new SchemaStatResponse(6, 42, 1.8);
    }
}
