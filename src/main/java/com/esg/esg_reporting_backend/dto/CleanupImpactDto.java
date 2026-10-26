package com.esg.esg_reporting_backend.dto;

import java.util.List;

public class CleanupImpactDto {

    private String targetType;
    private Long targetId;
    private String targetName;
    private String status;
    private boolean approved;

    private int organizationsAffected;
    private int periodsAffected;
    private int metricEntriesAffected;
    private int evidenceFilesAffected;
    private int auditLogsPreserved;

    private List<String> details;

    public CleanupImpactDto() {}

    public String getTargetType() { return targetType; }
    public void setTargetType(String targetType) { this.targetType = targetType; }

    public Long getTargetId() { return targetId; }
    public void setTargetId(Long targetId) { this.targetId = targetId; }

    public String getTargetName() { return targetName; }
    public void setTargetName(String targetName) { this.targetName = targetName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isApproved() { return approved; }
    public void setApproved(boolean approved) { this.approved = approved; }

    public int getOrganizationsAffected() { return organizationsAffected; }
    public void setOrganizationsAffected(int organizationsAffected) { this.organizationsAffected = organizationsAffected; }

    public int getPeriodsAffected() { return periodsAffected; }
    public void setPeriodsAffected(int periodsAffected) { this.periodsAffected = periodsAffected; }

    public int getMetricEntriesAffected() { return metricEntriesAffected; }
    public void setMetricEntriesAffected(int metricEntriesAffected) { this.metricEntriesAffected = metricEntriesAffected; }

    public int getEvidenceFilesAffected() { return evidenceFilesAffected; }
    public void setEvidenceFilesAffected(int evidenceFilesAffected) { this.evidenceFilesAffected = evidenceFilesAffected; }

    public int getAuditLogsPreserved() { return auditLogsPreserved; }
    public void setAuditLogsPreserved(int auditLogsPreserved) { this.auditLogsPreserved = auditLogsPreserved; }

    public List<String> getDetails() { return details; }
    public void setDetails(List<String> details) { this.details = details; }
}
