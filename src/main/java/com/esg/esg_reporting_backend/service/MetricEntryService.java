package com.esg.esg_reporting_backend.service;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.esg.esg_reporting_backend.dto.MetricEntryRequest;
import com.esg.esg_reporting_backend.entity.MetricEntry;
import com.esg.esg_reporting_backend.repo.MetricDefinitionRepository;
import com.esg.esg_reporting_backend.repo.MetricEntryRepository;
import com.esg.esg_reporting_backend.repo.ReportingPeriodRepository;

@Service
public class MetricEntryService {

    private static final Set<String> DATA_STATUSES =
            Set.of("PROVIDED", "ESTIMATED",
                   "UNAVAILABLE", "NOT_APPLICABLE");

    private final MetricEntryRepository entryRepository;
    private final MetricDefinitionRepository definitionRepository;
    private final ReportingPeriodRepository periodRepository;

    public MetricEntryService(
            MetricEntryRepository entryRepository,
            MetricDefinitionRepository definitionRepository,
            ReportingPeriodRepository periodRepository) {
        this.entryRepository = entryRepository;
        this.definitionRepository = definitionRepository;
        this.periodRepository = periodRepository;
    }

    @Transactional
    public MetricEntry create(
            Long periodId, MetricEntryRequest request) {

        String status = request.dataStatus().toUpperCase();

        if (!DATA_STATUSES.contains(status)) {
            throw new IllegalArgumentException(
                    "Invalid data status");
        }

        if (status.equals("UNAVAILABLE")
                || status.equals("NOT_APPLICABLE")) {
            throw new IllegalArgumentException(
                    "Unavailable or not applicable entries need a reason; " +
                    "we will add explicit reason fields next");
        }

        var period = periodRepository.findById(periodId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Reporting period not found"));

        var definition = definitionRepository
                .findById(request.metricDefinitionId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Metric definition not found"));

        MetricEntry entry = new MetricEntry();
        entry.setReportingPeriod(period);
        entry.setMetricDefinition(definition);
        entry.setValue(request.value());
        entry.setDataStatus(status);
        entry.setDataSource(request.dataSource());
        entry.setAssumptions(request.assumptions());

        return entryRepository.save(entry);
    }

    public List<MetricEntry> getByPeriod(Long periodId) {
        if (!periodRepository.existsById(periodId)) {
            throw new IllegalArgumentException(
                    "Reporting period not found");
        }

        return entryRepository.findByReportingPeriodId(periodId);
    }
}