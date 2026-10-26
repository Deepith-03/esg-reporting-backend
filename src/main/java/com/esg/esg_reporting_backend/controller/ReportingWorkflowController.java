
package com.esg.esg_reporting_backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.esg.esg_reporting_backend.entity.ReportingPeriod;
import com.esg.esg_reporting_backend.service.ReportingWorkflowService;

@RestController
@RequestMapping("/api/reporting-periods")
public class ReportingWorkflowController {

    private final ReportingWorkflowService workflowService;

    public ReportingWorkflowController(
            ReportingWorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    @PostMapping("/{periodId}/submit")
    public ResponseEntity<ReportingPeriod> submit(
            @PathVariable Long periodId,
            @RequestBody(required = false) WorkflowCommentRequest request,
            Authentication authentication) {

        return execute(() -> workflowService.submit(
                periodId, getUsername(authentication), getComment(request)));
    }

    @PostMapping("/{periodId}/request-changes")
    public ResponseEntity<ReportingPeriod> requestChanges(
            @PathVariable Long periodId,
            @RequestBody(required = false) WorkflowCommentRequest request,
            Authentication authentication) {

        return execute(() -> workflowService.requestChanges(
                periodId, getUsername(authentication), getComment(request)));
    }

    @PostMapping("/{periodId}/approve")
    public ResponseEntity<ReportingPeriod> approve(
            @PathVariable Long periodId,
            @RequestBody(required = false) WorkflowCommentRequest request,
            Authentication authentication) {

        return execute(() -> workflowService.approve(
                periodId, getUsername(authentication), getComment(request)));
    }

    @PostMapping("/{periodId}/resubmit")
    public ResponseEntity<ReportingPeriod> resubmit(
            @PathVariable Long periodId,
            @RequestBody(required = false) WorkflowCommentRequest request,
            Authentication authentication) {

        return execute(() -> workflowService.resubmit(
                periodId, getUsername(authentication), getComment(request)));
    }

    private String getUsername(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Authentication required");
        }

        return authentication.getName();
    }

    private String getComment(WorkflowCommentRequest request) {
        return request == null ? null : request.comment();
    }

    private ResponseEntity<ReportingPeriod> execute(
            WorkflowAction action) {
        try {
            return ResponseEntity.ok(action.run());
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, exception.getMessage());
        } catch (IllegalStateException exception) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, exception.getMessage());
        }
    }

    public record WorkflowCommentRequest(String comment) {
    }

    @FunctionalInterface
    private interface WorkflowAction {
        ReportingPeriod run();
    }
}
