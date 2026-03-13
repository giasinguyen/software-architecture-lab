package com.iuh.fit.demo.domain.plugin;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "cms_plugins")
public class PluginDescriptor {

    @Id
    @Column(name = "plugin_id", nullable = false, length = 100)
    private String pluginId;

    @Column(nullable = false, length = 20)
    private String version;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PluginStatus status;

    @Column(nullable = false, length = 100)
    private String owner;

    @Column(length = 255)
    private String description;

    @Column(name = "last_activated_at")
    private LocalDateTime lastActivatedAt;

    @Column(name = "last_deactivated_at")
    private LocalDateTime lastDeactivatedAt;

    protected PluginDescriptor() {
    }

    public PluginDescriptor(String pluginId, String version, PluginStatus status, String owner, String description) {
        this.pluginId = pluginId;
        this.version = version;
        this.status = status;
        this.owner = owner;
        this.description = description;
    }

    public String getPluginId() {
        return pluginId;
    }

    public String getVersion() {
        return version;
    }

    public PluginStatus getStatus() {
        return status;
    }

    public String getOwner() {
        return owner;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getLastActivatedAt() {
        return lastActivatedAt;
    }

    public LocalDateTime getLastDeactivatedAt() {
        return lastDeactivatedAt;
    }

    public void activate() {
        this.status = PluginStatus.ACTIVE;
        this.lastActivatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        this.status = PluginStatus.INACTIVE;
        this.lastDeactivatedAt = LocalDateTime.now();
    }
}
