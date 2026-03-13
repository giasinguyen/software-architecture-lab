package com.iuh.fit.demo.presentation;

import com.iuh.fit.demo.service.dto.EventFlowStatResponse;
import com.iuh.fit.demo.service.dto.FeatureCapabilityResponse;
import com.iuh.fit.demo.service.dto.PluginSummaryResponse;
import com.iuh.fit.demo.service.dto.SchemaStatResponse;
import com.iuh.fit.demo.service.dto.WorkflowStatResponse;
import com.iuh.fit.demo.service.overview.OverviewService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/overview")
public class OverviewController {

    private final OverviewService overviewService;

    public OverviewController(OverviewService overviewService) {
        this.overviewService = overviewService;
    }

    @GetMapping("/plugins")
    public List<PluginSummaryResponse> getPlugins() {
        return overviewService.getPluginOverview();
    }

    @GetMapping("/features")
    public List<FeatureCapabilityResponse> getFeatures() {
        return overviewService.getFeatureOverview();
    }

    @GetMapping("/events")
    public List<EventFlowStatResponse> getEvents() {
        return overviewService.getEventOverview();
    }

    @GetMapping("/workflow")
    public List<WorkflowStatResponse> getWorkflow() {
        return overviewService.getWorkflowOverview();
    }

    @GetMapping("/schema")
    public SchemaStatResponse getSchema() {
        return overviewService.getSchemaOverview();
    }
}
