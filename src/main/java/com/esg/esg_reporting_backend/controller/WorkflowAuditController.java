
package com.esg.esg_reporting_backend.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.esg.esg_reporting_backend.repo.ReportingPeriodRepository;
import com.esg.esg_reporting_backend.repo.WorkflowAuditRepository;

@RestController
@RequestMapping("/api/reporting-periods")
public class WorkflowAuditController {

    private final WorkflowAuditRepository workflowAuditRepository;
    private final ReportingPeriodRepository reportingPeriodRepository;

    public WorkflowAuditController(
            WorkflowAuditRepository workflowAuditRepository,
            ReportingPeriodRepository reportingPeriodRepository) {
        this.workflowAuditRepository = workflowAuditRepository;
        this.reportingPeriodRepository = reportingPeriodRepository;
    }

    @GetMapping("/{periodId}/audit")
    public List<AuditResponse> getAuditHistory(
            @PathVariable Long periodId) {

        if (!reportingPeriodRepository.existsById(periodId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Reporting period not found: " + periodId);
        }

        return workflowAuditRepository
                .findByReportingPeriodIdOrderByPerformedAtDesc(periodId)
                .stream()
                .map(audit -> new AuditResponse(
                        audit.getId(),
                        audit.getPreviousStatus(),
                        audit.getNewStatus(),
                        audit.getComment(),
                        audit.getPerformedBy(),
                        audit.getPerformedAt()))
                .toList();
    }

    public record AuditResponse(
            Long id,
            String previousStatus,
            String newStatus,
            String comment,
            String performedBy,
            LocalDateTime performedAt) {
    }
}
