package com.iuh.fit.demo.service.microkernel;

import com.iuh.fit.demo.service.dto.EventFlowStatResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventBusMetricsService {

    public List<EventFlowStatResponse> getEventFlowStats() {
        return List.of(
                new EventFlowStatResponse("CONTENT_SUBMITTED", "content-editor", "workflow-engine", 99.7, 45),
                new EventFlowStatResponse("CONTENT_APPROVED", "workflow-engine", "content-editor", 99.9, 38),
                new EventFlowStatResponse("SCHEMA_UPDATED", "dynamic-schema", "content-editor", 99.5, 62)
        );
    }
}
