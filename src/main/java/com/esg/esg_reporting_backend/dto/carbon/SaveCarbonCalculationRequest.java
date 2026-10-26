package com.esg.esg_reporting_backend.dto.carbon;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class SaveCarbonCalculationRequest {

    @NotNull(message = "Organization ID is required")
    private Long organizationId;

    private Long reportingPeriodId; // optional

    @NotBlank(message = "Calculation title is required")
    @Size(max = 200, message = "Title cannot exceed 200 characters")
    private String title;

    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    private String notes;

    @NotNull(message = "Calculation parameters are required")
    @Valid
    private CarbonCalculationRequest calculationRequest;

    public SaveCarbonCalculationRequest() {}

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getReportingPeriodId() {
        return reportingPeriodId;
    }

    public void setReportingPeriodId(Long reportingPeriodId) {
        this.reportingPeriodId = reportingPeriodId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public CarbonCalculationRequest getCalculationRequest() {
        return calculationRequest;
    }

    public void setCalculationRequest(CarbonCalculationRequest calculationRequest) {
        this.calculationRequest = calculationRequest;
    }
}
