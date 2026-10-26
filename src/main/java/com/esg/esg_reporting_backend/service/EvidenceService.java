
package com.esg.esg_reporting_backend.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.esg.esg_reporting_backend.entity.Evidence;
import com.esg.esg_reporting_backend.entity.MetricEntry;
import com.esg.esg_reporting_backend.repo.EvidenceRepository;
import com.esg.esg_reporting_backend.repo.MetricEntryRepository;

@Service
public class EvidenceService {

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "application/pdf",
            "image/jpeg",
            "image/png",
            "text/plain"
    );

    private final EvidenceRepository evidenceRepository;
    private final MetricEntryRepository metricEntryRepository;
    private final Path uploadDirectory;

    public EvidenceService(
            EvidenceRepository evidenceRepository,
            MetricEntryRepository metricEntryRepository,
            @Value("${app.evidence.upload-dir:uploads/evidence}")
            String uploadDirectory) {

        this.evidenceRepository = evidenceRepository;
        this.metricEntryRepository = metricEntryRepository;
        this.uploadDirectory = Paths.get(uploadDirectory)
                .toAbsolutePath()
                .normalize();
    }

    @Transactional
    public Evidence uploadEvidence(
            Long metricEntryId,
            MultipartFile file,
            String description) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Please select a file");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException(
                    "File size cannot exceed 10 MB");
        }

        String contentType = file.getContentType();

        if (contentType == null ||
                !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new IllegalArgumentException(
                    "Only PDF, JPEG, PNG and plain text files are allowed");
        }

        MetricEntry metricEntry = metricEntryRepository
                .findById(metricEntryId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Metric entry not found: " + metricEntryId));

        Files.createDirectories(uploadDirectory);

        String originalFileName = file.getOriginalFilename();
        String safeFileName = originalFileName == null
                ? "evidence"
                : Paths.get(originalFileName)
                        .getFileName()
                        .toString();

        String storageKey = UUID.randomUUID().toString();

        Path destination = uploadDirectory.resolve(storageKey).normalize();

        if (!destination.startsWith(uploadDirectory)) {
            throw new IllegalArgumentException("Invalid file path");
        }

        boolean fileStored = false;

        try {
            file.transferTo(destination);
            fileStored = true;

            Evidence evidence = new Evidence();
            evidence.setFileName(safeFileName);
            evidence.setContentType(contentType);
            evidence.setFileSize(file.getSize());
            evidence.setStorageKey(storageKey);
            evidence.setDescription(description);
            evidence.setMetricEntry(metricEntry);

            return evidenceRepository.save(evidence);
        } catch (IOException | RuntimeException exception) {
            if (fileStored) {
                Files.deleteIfExists(destination);
            }
            throw exception;
        }
    }

    @Transactional(readOnly = true)
    public List<Evidence> getEvidenceForMetricEntry(Long metricEntryId) {

        if (!metricEntryRepository.existsById(metricEntryId)) {
            throw new IllegalArgumentException(
                    "Metric entry not found: " + metricEntryId);
        }

        return evidenceRepository.findByMetricEntryId(metricEntryId);
    }
}
