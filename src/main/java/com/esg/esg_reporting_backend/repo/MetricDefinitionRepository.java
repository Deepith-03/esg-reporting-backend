package com.esg.esg_reporting_backend.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.esg.esg_reporting_backend.entity.MetricDefinition;

public interface MetricDefinitionRepository
        extends JpaRepository<MetricDefinition, Long> {

    List<MetricDefinition> findByPillarIgnoreCase(String pillar);

    List<MetricDefinition> findByActiveTrue();
}