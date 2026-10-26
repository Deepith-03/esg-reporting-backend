
package com.esg.esg_reporting_backend.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.esg.esg_reporting_backend.entity.Evidence;
import com.esg.esg_reporting_backend.service.EvidenceService;

@RestController
@RequestMapping("/api/metric-entries/{metricEntryId}/evidence")
public class EvidenceController {

    private final EvidenceService evidenceService;

    public EvidenceController(EvidenceService evidenceService) {
        this.evidenceService = evidenceService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EvidenceResponse> uploadEvidence(
            @PathVariable Long metricEntryId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String description) {

        try {
            Evidence evidence = evidenceService.uploadEvidence(
                    metricEntryId, file, description);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(toResponse(evidence));

        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, exception.getMessage());
        } catch (IOException exception) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Could not store evidence file");
        }
    }

    @GetMapping
    public List<EvidenceResponse> getEvidence(
            @PathVariable Long metricEntryId) {

        try {
            return evidenceService.getEvidenceForMetricEntry(metricEntryId)
                    .stream()
                    .map(this::toResponse)
                    .toList();

        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, exception.getMessage());
        }
    }

    private EvidenceResponse toResponse(Evidence evidence) {
        return new EvidenceResponse(
                evidence.getId(),
                evidence.getFileName(),
                evidence.getContentType(),
                evidence.getFileSize(),
                evidence.getDescription(),
                evidence.getUploadedAt()
        );
    }

    public record EvidenceResponse(
            Long id,
            String fileName,
            String contentType,
            Long fileSize,
            String description,
            java.time.LocalDateTime uploadedAt) {
    }
}
