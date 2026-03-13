package com.iuh.fit.demo.presentation;

import com.iuh.fit.demo.service.dto.PluginSummaryResponse;
import com.iuh.fit.demo.service.microkernel.PluginRegistryService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/plugins")
public class PluginController {

    private final PluginRegistryService pluginRegistryService;

    public PluginController(PluginRegistryService pluginRegistryService) {
        this.pluginRegistryService = pluginRegistryService;
    }

    @GetMapping
    public List<PluginSummaryResponse> getAllPlugins() {
        return pluginRegistryService.getPlugins();
    }

    @PostMapping("/{pluginId}/activate")
    public PluginSummaryResponse activate(@PathVariable String pluginId) {
        return pluginRegistryService.activate(pluginId);
    }

    @PostMapping("/{pluginId}/deactivate")
    public PluginSummaryResponse deactivate(@PathVariable String pluginId) {
        return pluginRegistryService.deactivate(pluginId);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFound(IllegalArgumentException ex) {
        return Map.of("message", ex.getMessage());
    }
}
