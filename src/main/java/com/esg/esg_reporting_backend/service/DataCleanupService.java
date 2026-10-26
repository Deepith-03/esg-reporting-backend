package com.esg.esg_reporting_backend.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.esg.esg_reporting_backend.dto.CleanupImpactDto;
import com.esg.esg_reporting_backend.dto.CleanupResultDto;
import com.esg.esg_reporting_backend.entity.Evidence;
import com.esg.esg_reporting_backend.entity.MetricEntry;
import com.esg.esg_reporting_backend.entity.Organization;
import com.esg.esg_reporting_backend.entity.ReportingPeriod;
import com.esg.esg_reporting_backend.repo.EvidenceRepository;
import com.esg.esg_reporting_backend.repo.MetricEntryRepository;
import com.esg.esg_reporting_backend.repo.OrganizationRepository;
import com.esg.esg_reporting_backend.repo.ReportingPeriodRepository;
import com.esg.esg_reporting_backend.repo.WorkflowAuditRepository;

@Service
public class DataCleanupService {

    private final OrganizationRepository organizationRepository;
    private final ReportingPeriodRepository reportingPeriodRepository;
    private final MetricEntryRepository metricEntryRepository;
    private final EvidenceRepository evidenceRepository;
    private final WorkflowAuditRepository workflowAuditRepository;
    private final com.esg.esg_reporting_backend.repo.CarbonCalculationRepository carbonCalculationRepository;
    private final Path uploadDirectory;

    public DataCleanupService(
            OrganizationRepository organizationRepository,
            ReportingPeriodRepository reportingPeriodRepository,
            MetricEntryRepository metricEntryRepository,
            EvidenceRepository evidenceRepository,
            WorkflowAuditRepository workflowAuditRepository,
            com.esg.esg_reporting_backend.repo.CarbonCalculationRepository carbonCalculationRepository,
            @Value("${app.evidence.upload-dir:uploads/evidence}") String uploadDirectory) {
        this.organizationRepository = organizationRepository;
        this.reportingPeriodRepository = reportingPeriodRepository;
        this.metricEntryRepository = metricEntryRepository;
        this.evidenceRepository = evidenceRepository;
        this.workflowAuditRepository = workflowAuditRepository;
        this.carbonCalculationRepository = carbonCalculationRepository;
        this.uploadDirectory = Paths.get(uploadDirectory).toAbsolutePath().normalize();
    }

    // --- PREVIEW METHODS ---

    @Transactional(readOnly = true)
    public CleanupImpactDto previewOrganizationCleanup(Long orgId) {
        Organization org = organizationRepository.findById(orgId)
                .orElseThrow(() -> new IllegalArgumentException("Organization not found: " + orgId));

        List<ReportingPeriod> periods = reportingPeriodRepository.findByOrganizationId(orgId);
        int entryCount = 0;
        int evidenceCount = 0;
        int auditCount = 0;
        boolean hasApproved = false;

        List<String> details = new ArrayList<>();
        details.add("Organization: " + org.getName() + " (" + org.getIndustry() + ")");

        for (ReportingPeriod p : periods) {
            if ("APPROVED".equalsIgnoreCase(p.getStatus())) {
                hasApproved = true;
            }
            List<MetricEntry> entries = metricEntryRepository.findByReportingPeriodId(p.getId());
            entryCount += entries.size();
            for (MetricEntry entry : entries) {
                evidenceCount += evidenceRepository.findByMetricEntryId(entry.getId()).size();
            }
            auditCount += workflowAuditRepository.findByReportingPeriodIdOrderByPerformedAtDesc(p.getId()).size();
            details.add("Period: " + p.getName() + " [Status: " + p.getStatus() + ", " + entries.size() + " entries]");
        }

        CleanupImpactDto impact = new CleanupImpactDto();
        impact.setTargetType("ORGANIZATION");
        impact.setTargetId(orgId);
        impact.setTargetName(org.getName());
        impact.setApproved(hasApproved);
        impact.setStatus(hasApproved ? "CONTAINS_APPROVED_PERIODS" : "ACTIVE");
        impact.setOrganizationsAffected(1);
        impact.setPeriodsAffected(periods.size());
        impact.setMetricEntriesAffected(entryCount);
        impact.setEvidenceFilesAffected(evidenceCount);
        impact.setAuditLogsPreserved(auditCount);
        impact.setDetails(details);
        return impact;
    }

    @Transactional(readOnly = true)
    public CleanupImpactDto previewPeriodCleanup(Long periodId) {
        ReportingPeriod p = reportingPeriodRepository.findById(periodId)
                .orElseThrow(() -> new IllegalArgumentException("Reporting period not found: " + periodId));

        List<MetricEntry> entries = metricEntryRepository.findByReportingPeriodId(periodId);
        int evidenceCount = 0;
        for (MetricEntry entry : entries) {
            evidenceCount += evidenceRepository.findByMetricEntryId(entry.getId()).size();
        }
        int auditCount = workflowAuditRepository.findByReportingPeriodIdOrderByPerformedAtDesc(periodId).size();
        boolean isApproved = "APPROVED".equalsIgnoreCase(p.getStatus());

        List<String> details = new ArrayList<>();
        details.add("Reporting Period: " + p.getName() + " [Status: " + p.getStatus() + "]");
        details.add("Organization: " + p.getOrganization().getName());
        details.add("Metric Entries: " + entries.size());
        details.add("Evidence Files: " + evidenceCount);
        details.add("Audit logs that will be preserved: " + auditCount);

        CleanupImpactDto impact = new CleanupImpactDto();
        impact.setTargetType("REPORTING_PERIOD");
        impact.setTargetId(periodId);
        impact.setTargetName(p.getName());
        impact.setStatus(p.getStatus());
        impact.setApproved(isApproved);
        impact.setOrganizationsAffected(0);
        impact.setPeriodsAffected(1);
        impact.setMetricEntriesAffected(entries.size());
        impact.setEvidenceFilesAffected(evidenceCount);
        impact.setAuditLogsPreserved(auditCount);
        impact.setDetails(details);
        return impact;
    }

    @Transactional(readOnly = true)
    public CleanupImpactDto previewMetricEntryCleanup(Long entryId) {
        MetricEntry entry = metricEntryRepository.findById(entryId)
                .orElseThrow(() -> new IllegalArgumentException("Metric entry not found: " + entryId));

        List<Evidence> evidenceList = evidenceRepository.findByMetricEntryId(entryId);
        ReportingPeriod p = entry.getReportingPeriod();
        boolean isApproved = p != null && "APPROVED".equalsIgnoreCase(p.getStatus());

        List<String> details = new ArrayList<>();
        details.add("Metric: " + (entry.getMetricDefinition() != null ? entry.getMetricDefinition().getName() : "ID #" + entryId));
        details.add("Value: " + entry.getValue());
        details.add("Period: " + (p != null ? p.getName() + " (" + p.getStatus() + ")" : "N/A"));
        details.add("Associated Evidence: " + evidenceList.size() + " files");

        CleanupImpactDto impact = new CleanupImpactDto();
        impact.setTargetType("METRIC_ENTRY");
        impact.setTargetId(entryId);
        impact.setTargetName(entry.getMetricDefinition() != null ? entry.getMetricDefinition().getName() : "Entry #" + entryId);
        impact.setStatus(p != null ? p.getStatus() : "ACTIVE");
        impact.setApproved(isApproved);
        impact.setOrganizationsAffected(0);
        impact.setPeriodsAffected(0);
        impact.setMetricEntriesAffected(1);
        impact.setEvidenceFilesAffected(evidenceList.size());
        impact.setAuditLogsPreserved(0);
        impact.setDetails(details);
        return impact;
    }

    @Transactional(readOnly = true)
    public CleanupImpactDto previewEvidenceCleanup(Long evidenceId) {
        Evidence ev = evidenceRepository.findById(evidenceId)
                .orElseThrow(() -> new IllegalArgumentException("Evidence not found: " + evidenceId));

        MetricEntry entry = ev.getMetricEntry();
        ReportingPeriod p = entry != null ? entry.getReportingPeriod() : null;
        boolean isApproved = p != null && "APPROVED".equalsIgnoreCase(p.getStatus());

        List<String> details = new ArrayList<>();
        details.add("File Name: " + ev.getFileName() + " (" + (ev.getFileSize() / 1024) + " KB)");
        details.add("Description: " + (ev.getDescription() != null ? ev.getDescription() : "None"));
        details.add("Belongs to Period: " + (p != null ? p.getName() + " (" + p.getStatus() + ")" : "N/A"));

        CleanupImpactDto impact = new CleanupImpactDto();
        impact.setTargetType("EVIDENCE");
        impact.setTargetId(evidenceId);
        impact.setTargetName(ev.getFileName());
        impact.setStatus(p != null ? p.getStatus() : "ACTIVE");
        impact.setApproved(isApproved);
        impact.setOrganizationsAffected(0);
        impact.setPeriodsAffected(0);
        impact.setMetricEntriesAffected(0);
        impact.setEvidenceFilesAffected(1);
        impact.setAuditLogsPreserved(0);
        impact.setDetails(details);
        return impact;
    }

    @Transactional(readOnly = true)
    public CleanupImpactDto previewClearDemoData() {
        List<Organization> allOrgs = organizationRepository.findAll();
        List<Organization> demoOrgs = allOrgs.stream()
                .filter(this::isDemoOrganization)
                .toList();

        int periodCount = 0;
        int entryCount = 0;
        int evidenceCount = 0;
        int auditCount = 0;
        List<String> details = new ArrayList<>();

        for (Organization org : demoOrgs) {
            List<ReportingPeriod> periods = reportingPeriodRepository.findByOrganizationId(org.getId());
            periodCount += periods.size();
            for (ReportingPeriod p : periods) {
                List<MetricEntry> entries = metricEntryRepository.findByReportingPeriodId(p.getId());
                entryCount += entries.size();
                for (MetricEntry e : entries) {
                    evidenceCount += evidenceRepository.findByMetricEntryId(e.getId()).size();
                }
                auditCount += workflowAuditRepository.findByReportingPeriodIdOrderByPerformedAtDesc(p.getId()).size();
            }
            details.add("Demo Org: " + org.getName() + " (Industry: " + org.getIndustry() + ", " + periods.size() + " periods)");
        }

        CleanupImpactDto impact = new CleanupImpactDto();
        impact.setTargetType("DEMO_DATA");
        impact.setTargetName("All Test / Demo Organizations & Data");
        impact.setApproved(false);
        impact.setStatus("DEMO_BATCH");
        impact.setOrganizationsAffected(demoOrgs.size());
        impact.setPeriodsAffected(periodCount);
        impact.setMetricEntriesAffected(entryCount);
        impact.setEvidenceFilesAffected(evidenceCount);
        impact.setAuditLogsPreserved(auditCount);
        impact.setDetails(details);
        return impact;
    }

    // --- DELETION METHODS ---

    @Transactional
    public CleanupResultDto deleteEvidence(Long evidenceId, boolean allowApprovedOverride) {
        Evidence ev = evidenceRepository.findById(evidenceId)
                .orElseThrow(() -> new IllegalArgumentException("Evidence not found: " + evidenceId));

        MetricEntry entry = ev.getMetricEntry();
        ReportingPeriod p = entry != null ? entry.getReportingPeriod() : null;
        if (p != null && "APPROVED".equalsIgnoreCase(p.getStatus()) && !allowApprovedOverride) {
            throw new IllegalStateException("Cannot delete evidence for APPROVED reporting period '" + p.getName() + "' without explicit override.");
        }

        deletePhysicalFile(ev.getStorageKey());
        evidenceRepository.delete(ev);

        CleanupResultDto result = new CleanupResultDto();
        result.setSuccess(true);
        result.setMessage("Evidence file '" + ev.getFileName() + "' deleted successfully.");
        result.setEvidenceFilesDeleted(1);
        return result;
    }

    @Transactional
    public CleanupResultDto deleteMetricEntry(Long entryId, boolean allowApprovedOverride) {
        MetricEntry entry = metricEntryRepository.findById(entryId)
                .orElseThrow(() -> new IllegalArgumentException("Metric entry not found: " + entryId));

        ReportingPeriod p = entry.getReportingPeriod();
        if (p != null && "APPROVED".equalsIgnoreCase(p.getStatus()) && !allowApprovedOverride) {
            throw new IllegalStateException("Cannot delete metric entry for APPROVED reporting period '" + p.getName() + "' without explicit override.");
        }

        List<Evidence> evidenceList = evidenceRepository.findByMetricEntryId(entryId);
        for (Evidence ev : evidenceList) {
            deletePhysicalFile(ev.getStorageKey());
            evidenceRepository.delete(ev);
        }

        metricEntryRepository.delete(entry);

        CleanupResultDto result = new CleanupResultDto();
        result.setSuccess(true);
        result.setMessage("Metric entry deleted successfully.");
        result.setMetricEntriesDeleted(1);
        result.setEvidenceFilesDeleted(evidenceList.size());
        return result;
    }

    @Transactional
    public CleanupResultDto deleteReportingPeriod(Long periodId, boolean allowApprovedOverride) {
        ReportingPeriod p = reportingPeriodRepository.findById(periodId)
                .orElseThrow(() -> new IllegalArgumentException("Reporting period not found: " + periodId));

        if ("APPROVED".equalsIgnoreCase(p.getStatus()) && !allowApprovedOverride) {
            throw new IllegalStateException("Cannot delete APPROVED reporting period '" + p.getName() + "' without explicit override.");
        }

        // Delete metric entries and their evidence first
        List<MetricEntry> entries = metricEntryRepository.findByReportingPeriodId(periodId);
        int evidenceCount = 0;
        for (MetricEntry entry : entries) {
            List<Evidence> evidenceList = evidenceRepository.findByMetricEntryId(entry.getId());
            evidenceCount += evidenceList.size();
            for (Evidence ev : evidenceList) {
                deletePhysicalFile(ev.getStorageKey());
                evidenceRepository.delete(ev);
            }
            metricEntryRepository.delete(entry);
        }

        // Preserve audit records: we do not delete audit records. However, because WorkflowAudit has a foreign key to reporting_periods,
        // we disassociate or leave audit entries if reporting period cannot be deleted, OR we note that audit history is preserved.
        // Wait: WorkflowAudit has nullable = false for reporting_period_id.
        // If audit logs must not be deleted as per requirement #8, we can either:
        // - In our delete logic, check if audits exist. If audits exist, we do NOT delete audit records. But MySQL foreign key will fail if reporting_period is deleted!
        // To strictly respect requirement #8 ("Do not delete audit logs as part of ordinary data cleanup") and foreign keys:
        // When deleting a reporting period that has workflow audits, we must either prevent period deletion or preserve audits.
        // Let's check how many audits exist:
        int preservedAudits = workflowAuditRepository.findByReportingPeriodIdOrderByPerformedAtDesc(periodId).size();
        if (preservedAudits > 0) {
            // If the period has formal workflow audit logs, we disallow regular deletion of the period itself unless explicit,
            // or we preserve the audit log by unlinking or setting a dummy archive period.
            // But let's check: can we delete periods without audits freely? Yes!
            // For periods WITH workflow audits, requirement #8 says: "Do not delete audit logs as part of ordinary data cleanup."
            // So if a period has audits, we throw: "Cannot delete reporting period with active workflow audit logs. Audit logs must be preserved."
            // Or if allowApprovedOverride is provided, can we cascade? No! "Do not delete audit logs as part of ordinary data cleanup."
            throw new IllegalStateException("Reporting period '" + p.getName() + "' contains " + preservedAudits +
                    " workflow audit log(s). Audit history must not be destroyed during data cleanup.");
        }

        carbonCalculationRepository.deleteByReportingPeriodId(periodId);
        reportingPeriodRepository.delete(p);

        CleanupResultDto result = new CleanupResultDto();
        result.setSuccess(true);
        result.setMessage("Reporting period '" + p.getName() + "' deleted successfully.");
        result.setPeriodsDeleted(1);
        result.setMetricEntriesDeleted(entries.size());
        result.setEvidenceFilesDeleted(evidenceCount);
        result.setAuditLogsPreserved(preservedAudits);
        return result;
    }

    @Transactional
    public CleanupResultDto deleteOrganization(Long orgId, boolean allowApprovedOverride) {
        Organization org = organizationRepository.findById(orgId)
                .orElseThrow(() -> new IllegalArgumentException("Organization not found: " + orgId));

        List<ReportingPeriod> periods = reportingPeriodRepository.findByOrganizationId(orgId);
        int entriesCount = 0;
        int evidenceCount = 0;

        for (ReportingPeriod p : periods) {
            if ("APPROVED".equalsIgnoreCase(p.getStatus()) && !allowApprovedOverride) {
                throw new IllegalStateException("Organization contains APPROVED period '" + p.getName() + "'. Cannot delete without explicit override.");
            }
            int audits = workflowAuditRepository.findByReportingPeriodIdOrderByPerformedAtDesc(p.getId()).size();
            if (audits > 0) {
                throw new IllegalStateException("Cannot delete organization: Reporting period '" + p.getName() +
                        "' contains " + audits + " audit log(s) that must be preserved.");
            }
        }

        // Clean up all carbon calculations belonging to this organization
        carbonCalculationRepository.deleteByOrganizationId(orgId);

        for (ReportingPeriod p : periods) {
            List<MetricEntry> entries = metricEntryRepository.findByReportingPeriodId(p.getId());
            entriesCount += entries.size();
            for (MetricEntry entry : entries) {
                List<Evidence> evidenceList = evidenceRepository.findByMetricEntryId(entry.getId());
                evidenceCount += evidenceList.size();
                for (Evidence ev : evidenceList) {
                    deletePhysicalFile(ev.getStorageKey());
                    evidenceRepository.delete(ev);
                }
                metricEntryRepository.delete(entry);
            }
            reportingPeriodRepository.delete(p);
        }

        organizationRepository.delete(org);

        CleanupResultDto result = new CleanupResultDto();
        result.setSuccess(true);
        result.setMessage("Organization '" + org.getName() + "' and its child data deleted successfully.");
        result.setOrganizationsDeleted(1);
        result.setPeriodsDeleted(periods.size());
        result.setMetricEntriesDeleted(entriesCount);
        result.setEvidenceFilesDeleted(evidenceCount);
        return result;
    }

    @Transactional
    public CleanupResultDto clearDemoData() {
        List<Organization> allOrgs = organizationRepository.findAll();
        List<Organization> demoOrgs = allOrgs.stream()
                .filter(this::isDemoOrganization)
                .toList();

        int orgsDeleted = 0;
        int periodsDeleted = 0;
        int entriesDeleted = 0;
        int evidenceDeleted = 0;
        int auditsPreserved = 0;
        List<String> notes = new ArrayList<>();

        for (Organization org : demoOrgs) {
            List<ReportingPeriod> periods = reportingPeriodRepository.findByOrganizationId(org.getId());
            boolean skipOrg = false;

            for (ReportingPeriod p : periods) {
                int audits = workflowAuditRepository.findByReportingPeriodIdOrderByPerformedAtDesc(p.getId()).size();
                if (audits > 0) {
                    skipOrg = true;
                    auditsPreserved += audits;
                    notes.add("Skipped period '" + p.getName() + "' in '" + org.getName() + "' because it has " + audits + " audit logs.");
                    break;
                }
            }

            if (skipOrg) {
                notes.add("Skipped organization '" + org.getName() + "' to preserve existing audit logs.");
                continue;
            }

            for (ReportingPeriod p : periods) {
                List<MetricEntry> entries = metricEntryRepository.findByReportingPeriodId(p.getId());
                entriesDeleted += entries.size();
                for (MetricEntry entry : entries) {
                    List<Evidence> evidenceList = evidenceRepository.findByMetricEntryId(entry.getId());
                    evidenceDeleted += evidenceList.size();
                    for (Evidence ev : evidenceList) {
                        deletePhysicalFile(ev.getStorageKey());
                        evidenceRepository.delete(ev);
                    }
                    metricEntryRepository.delete(entry);
                }
                reportingPeriodRepository.delete(p);
                periodsDeleted++;
            }

            carbonCalculationRepository.deleteByOrganizationId(org.getId());
            organizationRepository.delete(org);
            orgsDeleted++;
        }

        CleanupResultDto result = new CleanupResultDto();
        result.setSuccess(true);
        result.setMessage("Demo data cleanup completed. Removed " + orgsDeleted + " demo organization(s), " +
                periodsDeleted + " period(s), " + entriesDeleted + " entry/entries, " + evidenceDeleted + " evidence file(s).");
        result.setOrganizationsDeleted(orgsDeleted);
        result.setPeriodsDeleted(periodsDeleted);
        result.setMetricEntriesDeleted(entriesDeleted);
        result.setEvidenceFilesDeleted(evidenceDeleted);
        result.setAuditLogsPreserved(auditsPreserved);
        result.setNotes(notes);
        return result;
    }

    private boolean isDemoOrganization(Organization org) {
        String name = org.getName() != null ? org.getName().toLowerCase() : "";
        String industry = org.getIndustry() != null ? org.getIndustry().toLowerCase() : "";
        return name.contains("demo") || name.contains("test") || name.contains("sample") ||
                name.contains("generic h2") || name.contains("jjjj") || industry.contains("test") || industry.contains("abcde") || industry.contains("sss");
    }

    private void deletePhysicalFile(String storageKey) {
        if (storageKey == null || storageKey.isBlank()) return;
        try {
            Path target = uploadDirectory.resolve(storageKey).normalize();
            if (target.startsWith(uploadDirectory)) {
                Files.deleteIfExists(target);
            }
        } catch (IOException ignored) {
            // Continue transaction if file deletion fails
        }
    }
}
