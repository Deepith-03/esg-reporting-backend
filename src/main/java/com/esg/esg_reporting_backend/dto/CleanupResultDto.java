package com.esg.esg_reporting_backend.dto;

import java.util.List;

public class CleanupResultDto {

    private boolean success;
    private String message;
    private int organizationsDeleted;
    private int periodsDeleted;
    private int metricEntriesDeleted;
    private int evidenceFilesDeleted;
    private int auditLogsPreserved;
    private List<String> notes;

    public CleanupResultDto() {}

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public int getOrganizationsDeleted() { return organizationsDeleted; }
    public void setOrganizationsDeleted(int organizationsDeleted) { this.organizationsDeleted = organizationsDeleted; }

    public int getPeriodsDeleted() { return periodsDeleted; }
    public void setPeriodsDeleted(int periodsDeleted) { this.periodsDeleted = periodsDeleted; }

    public int getMetricEntriesDeleted() { return metricEntriesDeleted; }
    public void setMetricEntriesDeleted(int metricEntriesDeleted) { this.metricEntriesDeleted = metricEntriesDeleted; }

    public int getEvidenceFilesDeleted() { return evidenceFilesDeleted; }
    public void setEvidenceFilesDeleted(int evidenceFilesDeleted) { this.evidenceFilesDeleted = evidenceFilesDeleted; }

    public int getAuditLogsPreserved() { return auditLogsPreserved; }
    public void setAuditLogsPreserved(int auditLogsPreserved) { this.auditLogsPreserved = auditLogsPreserved; }

    public List<String> getNotes() { return notes; }
    public void setNotes(List<String> notes) { this.notes = notes; }
}
