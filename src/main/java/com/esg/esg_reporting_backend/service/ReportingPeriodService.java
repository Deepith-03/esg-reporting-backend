package com.esg.esg_reporting_backend.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.esg.esg_reporting_backend.dto.ReportingPeriodRequest;
import com.esg.esg_reporting_backend.entity.ReportingPeriod;
import com.esg.esg_reporting_backend.repo.OrganizationRepository;
import com.esg.esg_reporting_backend.repo.ReportingPeriodRepository;

@Service
public class ReportingPeriodService {

    private final ReportingPeriodRepository periodRepository;
    private final OrganizationRepository organizationRepository;

    public ReportingPeriodService(ReportingPeriodRepository periodRepository,OrganizationRepository organizationRepository) {
        this.periodRepository = periodRepository;
        this.organizationRepository = organizationRepository;
    }

    @Transactional
    public ReportingPeriod create(Long organizationId, ReportingPeriodRequest request) {

        if (request.endDate().isBefore(request.startDate())) {
            throw new IllegalArgumentException(
                    "End date must be on or after start date");
        }

        var organization = organizationRepository
                .findById(organizationId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Organization not found"));

        ReportingPeriod period = new ReportingPeriod();
        period.setName(request.name());
        period.setStartDate(request.startDate());
        period.setEndDate(request.endDate());
        period.setReportingBoundary(request.reportingBoundary());
        period.setOrganization(organization);

        return periodRepository.save(period);
    }

    public List<ReportingPeriod> findByOrganization(Long organizationId) {
        if (!organizationRepository.existsById(organizationId)) {
            throw new IllegalArgumentException(
                    "Organization not found");
        }

        return periodRepository.findByOrganizationId(organizationId);
    }
}