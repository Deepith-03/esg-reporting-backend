package com.esg.esg_reporting_backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.esg.esg_reporting_backend.dto.CleanupImpactDto;
import com.esg.esg_reporting_backend.dto.CleanupResultDto;
import com.esg.esg_reporting_backend.service.DataCleanupService;

@RestController
@RequestMapping("/api/admin/cleanup")
public class DataCleanupController {

    private final DataCleanupService cleanupService;

    public DataCleanupController(DataCleanupService cleanupService) {
        this.cleanupService = cleanupService;
    }

    // --- PREVIEW ENDPOINTS ---

    @GetMapping("/preview/organizations/{id}")
    public ResponseEntity<CleanupImpactDto> previewOrganization(@PathVariable Long id) {
        return execute(() -> cleanupService.previewOrganizationCleanup(id));
    }

    @GetMapping("/preview/periods/{id}")
    public ResponseEntity<CleanupImpactDto> previewPeriod(@PathVariable Long id) {
        return execute(() -> cleanupService.previewPeriodCleanup(id));
    }

    @GetMapping("/preview/metric-entries/{id}")
    public ResponseEntity<CleanupImpactDto> previewMetricEntry(@PathVariable Long id) {
        return execute(() -> cleanupService.previewMetricEntryCleanup(id));
    }

    @GetMapping("/preview/evidence/{id}")
    public ResponseEntity<CleanupImpactDto> previewEvidence(@PathVariable Long id) {
        return execute(() -> cleanupService.previewEvidenceCleanup(id));
    }

    @GetMapping("/preview/demo-data")
    public ResponseEntity<CleanupImpactDto> previewClearDemoData() {
        return execute(() -> cleanupService.previewClearDemoData());
    }

    // --- DELETION ENDPOINTS ---

    @DeleteMapping("/organizations/{id}")
    public ResponseEntity<CleanupResultDto> deleteOrganization(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean allowApprovedOverride) {
        return execute(() -> cleanupService.deleteOrganization(id, allowApprovedOverride));
    }

    @DeleteMapping("/periods/{id}")
    public ResponseEntity<CleanupResultDto> deleteReportingPeriod(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean allowApprovedOverride) {
        return execute(() -> cleanupService.deleteReportingPeriod(id, allowApprovedOverride));
    }

    @DeleteMapping("/metric-entries/{id}")
    public ResponseEntity<CleanupResultDto> deleteMetricEntry(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean allowApprovedOverride) {
        return execute(() -> cleanupService.deleteMetricEntry(id, allowApprovedOverride));
    }

    @DeleteMapping("/evidence/{id}")
    public ResponseEntity<CleanupResultDto> deleteEvidence(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean allowApprovedOverride) {
        return execute(() -> cleanupService.deleteEvidence(id, allowApprovedOverride));
    }

    @PostMapping("/clear-demo-data")
    public ResponseEntity<CleanupResultDto> clearDemoData() {
        return execute(() -> cleanupService.clearDemoData());
    }

    private <T> ResponseEntity<T> execute(Action<T> action) {
        try {
            return ResponseEntity.ok(action.run());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @FunctionalInterface
    private interface Action<T> {
        T run() throws Exception;
    }
}
