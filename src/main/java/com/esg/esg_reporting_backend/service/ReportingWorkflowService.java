
package com.esg.esg_reporting_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.esg.esg_reporting_backend.entity.ReportingPeriod;
import com.esg.esg_reporting_backend.entity.WorkflowAudit;
import com.esg.esg_reporting_backend.repo.ReportingPeriodRepository;
import com.esg.esg_reporting_backend.repo.WorkflowAuditRepository;

@Service
public class ReportingWorkflowService {

    private final ReportingPeriodRepository reportingPeriodRepository;
    private final WorkflowAuditRepository workflowAuditRepository;

    public ReportingWorkflowService(
            ReportingPeriodRepository reportingPeriodRepository,
            WorkflowAuditRepository workflowAuditRepository) {
        this.reportingPeriodRepository = reportingPeriodRepository;
        this.workflowAuditRepository = workflowAuditRepository;
    }

    @Transactional
    public ReportingPeriod submit(
            Long periodId, String performedBy, String comment) {

        return changeStatus(
                periodId, "DRAFT", "SUBMITTED",
                performedBy, comment);
    }

    @Transactional
    public ReportingPeriod requestChanges(
            Long periodId, String performedBy, String comment) {

        if (comment == null || comment.isBlank()) {
            throw new IllegalArgumentException(
                    "A comment is required when requesting changes");
        }

        return changeStatus(
                periodId, "SUBMITTED", "CHANGES_REQUESTED",
                performedBy, comment);
    }

    @Transactional
    public ReportingPeriod approve(
            Long periodId, String performedBy, String comment) {

        return changeStatus(
                periodId, "SUBMITTED", "APPROVED",
                performedBy, comment);
    }

    @Transactional
    public ReportingPeriod resubmit(
            Long periodId, String performedBy, String comment) {

        return changeStatus(
                periodId, "CHANGES_REQUESTED", "SUBMITTED",
                performedBy, comment);
    }

    private ReportingPeriod changeStatus(
            Long periodId,
            String expectedStatus,
            String newStatus,
            String performedBy,
            String comment) {

        if (performedBy == null || performedBy.isBlank()) {
            throw new IllegalArgumentException(
                    "Authenticated username is required");
        }

        ReportingPeriod period = reportingPeriodRepository
                .findById(periodId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Reporting period not found: " + periodId));

        String previousStatus = period.getStatus();

        if (previousStatus == null
                || !expectedStatus.equalsIgnoreCase(previousStatus)) {
            throw new IllegalStateException(
                    "Cannot change status from " + previousStatus
                    + " to " + newStatus);
        }

        period.setStatus(newStatus);
        ReportingPeriod savedPeriod =
                reportingPeriodRepository.save(period);

        WorkflowAudit audit = new WorkflowAudit();
        audit.setReportingPeriod(savedPeriod);
        audit.setPreviousStatus(previousStatus);
        audit.setNewStatus(newStatus);
        audit.setComment(comment);
        audit.setPerformedBy(performedBy);

        workflowAuditRepository.save(audit);

        return savedPeriod;
    }
}
