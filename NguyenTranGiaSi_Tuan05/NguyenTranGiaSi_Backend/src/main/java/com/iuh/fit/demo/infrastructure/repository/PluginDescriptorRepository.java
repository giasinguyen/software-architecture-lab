package com.iuh.fit.demo.infrastructure.repository;

import com.iuh.fit.demo.domain.plugin.PluginDescriptor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PluginDescriptorRepository extends JpaRepository<PluginDescriptor, String> {
}
