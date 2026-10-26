package com.esg.esg_reporting_backend.dto.carbon;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CarbonCalculationRecordDto {

    private Long id;
    private Long organizationId;
    private String organizationName;
    private Long reportingPeriodId;
    private String reportingPeriodName;
    private String title;
    private LocalDateTime calculatedAt;
    private String calculationMethod;
    private BigDecimal electricityKwh;
    private BigDecimal totalEmissionsKg;
    private BigDecimal totalEmissionsTonnes;
    private BigDecimal scope1EmissionsKg;
    private BigDecimal scope2EmissionsKg;
    private BigDecimal scope3EmissionsKg;
    private String notes;
    private String createdBy;
    private CarbonCalculationResponse fullResult;

    public CarbonCalculationRecordDto() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public Long getReportingPeriodId() {
        return reportingPeriodId;
    }

    public void setReportingPeriodId(Long reportingPeriodId) {
        this.reportingPeriodId = reportingPeriodId;
    }

    public String getReportingPeriodName() {
        return reportingPeriodName;
    }

    public void setReportingPeriodName(String reportingPeriodName) {
        this.reportingPeriodName = reportingPeriodName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getCalculatedAt() {
        return calculatedAt;
    }

    public void setCalculatedAt(LocalDateTime calculatedAt) {
        this.calculatedAt = calculatedAt;
    }

    public String getCalculationMethod() {
        return calculationMethod;
    }

    public void setCalculationMethod(String calculationMethod) {
        this.calculationMethod = calculationMethod;
    }

    public BigDecimal getElectricityKwh() {
        return electricityKwh;
    }

    public void setElectricityKwh(BigDecimal electricityKwh) {
        this.electricityKwh = electricityKwh;
    }

    public BigDecimal getTotalEmissionsKg() {
        return totalEmissionsKg;
    }

    public void setTotalEmissionsKg(BigDecimal totalEmissionsKg) {
        this.totalEmissionsKg = totalEmissionsKg;
    }

    public BigDecimal getTotalEmissionsTonnes() {
        return totalEmissionsTonnes;
    }

    public void setTotalEmissionsTonnes(BigDecimal totalEmissionsTonnes) {
        this.totalEmissionsTonnes = totalEmissionsTonnes;
    }

    public BigDecimal getScope1EmissionsKg() {
        return scope1EmissionsKg;
    }

    public void setScope1EmissionsKg(BigDecimal scope1EmissionsKg) {
        this.scope1EmissionsKg = scope1EmissionsKg;
    }

    public BigDecimal getScope2EmissionsKg() {
        return scope2EmissionsKg;
    }

    public void setScope2EmissionsKg(BigDecimal scope2EmissionsKg) {
        this.scope2EmissionsKg = scope2EmissionsKg;
    }

    public BigDecimal getScope3EmissionsKg() {
        return scope3EmissionsKg;
    }

    public void setScope3EmissionsKg(BigDecimal scope3EmissionsKg) {
        this.scope3EmissionsKg = scope3EmissionsKg;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public CarbonCalculationResponse getFullResult() {
        return fullResult;
    }

    public void setFullResult(CarbonCalculationResponse fullResult) {
        this.fullResult = fullResult;
    }
}
