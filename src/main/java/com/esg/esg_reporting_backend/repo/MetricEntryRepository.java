package com.esg.esg_reporting_backend.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.esg.esg_reporting_backend.entity.MetricEntry;

public interface MetricEntryRepository
        extends JpaRepository<MetricEntry, Long> {

    List<MetricEntry> findByReportingPeriodId(Long periodId);
}