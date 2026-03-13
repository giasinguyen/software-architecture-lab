package com.iuh.fit.demo.service.microkernel;

import com.iuh.fit.demo.domain.plugin.PluginDescriptor;
import com.iuh.fit.demo.domain.plugin.PluginStatus;
import com.iuh.fit.demo.infrastructure.repository.PluginDescriptorRepository;
import com.iuh.fit.demo.service.dto.PluginSummaryResponse;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PluginRegistryService implements ApplicationRunner {

    private final PluginDescriptorRepository pluginDescriptorRepository;

    public PluginRegistryService(PluginDescriptorRepository pluginDescriptorRepository) {
        this.pluginDescriptorRepository = pluginDescriptorRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        registerIfMissing("content-editor", "1.0.0", "content-team", "Rich text and media authoring plugin");
        registerIfMissing("workflow-engine", "1.0.0", "workflow-team", "Approval workflow state machine plugin");
        registerIfMissing("dynamic-schema", "1.0.0", "platform-team", "Runtime content-type and fields plugin");
    }

    @Transactional(readOnly = true)
    public List<PluginSummaryResponse> getPlugins() {
        return pluginDescriptorRepository.findAll().stream()
                .map(plugin -> new PluginSummaryResponse(
                        plugin.getPluginId(),
                        plugin.getVersion(),
                        plugin.getStatus(),
                        plugin.getOwner(),
                        plugin.getDescription(),
                        plugin.getLastActivatedAt(),
                        plugin.getLastDeactivatedAt()
                ))
                .toList();
    }

    @Transactional
    public PluginSummaryResponse activate(String pluginId) {
        PluginDescriptor plugin = pluginDescriptorRepository.findById(pluginId)
                .orElseThrow(() -> new IllegalArgumentException("Plugin not found: " + pluginId));
        plugin.activate();
        pluginDescriptorRepository.save(plugin);
        return toSummary(plugin);
    }

    @Transactional
    public PluginSummaryResponse deactivate(String pluginId) {
        PluginDescriptor plugin = pluginDescriptorRepository.findById(pluginId)
                .orElseThrow(() -> new IllegalArgumentException("Plugin not found: " + pluginId));
        plugin.deactivate();
        pluginDescriptorRepository.save(plugin);
        return toSummary(plugin);
    }

    @Transactional(readOnly = true)
    public boolean isPluginActive(String pluginId) {
        return pluginDescriptorRepository.findById(pluginId)
                .map(plugin -> plugin.getStatus() == PluginStatus.ACTIVE)
                .orElse(false);
    }

    private void registerIfMissing(String pluginId, String version, String owner, String description) {
        pluginDescriptorRepository.findById(pluginId)
                .orElseGet(() -> pluginDescriptorRepository.save(
                        new PluginDescriptor(pluginId, version, PluginStatus.INACTIVE, owner, description)
                ));
    }

    private PluginSummaryResponse toSummary(PluginDescriptor plugin) {
        return new PluginSummaryResponse(
                plugin.getPluginId(),
                plugin.getVersion(),
                plugin.getStatus(),
                plugin.getOwner(),
                plugin.getDescription(),
                plugin.getLastActivatedAt(),
                plugin.getLastDeactivatedAt()
        );
    }
}
