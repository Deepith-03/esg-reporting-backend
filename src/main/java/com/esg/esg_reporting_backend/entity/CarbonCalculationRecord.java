package com.esg.esg_reporting_backend.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "carbon_calculations")
public class CarbonCalculationRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "reporting_period_id", nullable = true)
    private ReportingPeriod reportingPeriod;

    @NotBlank
    @Column(nullable = false, length = 200)
    private String title;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime calculatedAt;

    @NotBlank
    @Column(nullable = false, length = 50)
    private String calculationMethod;

    @Column(precision = 19, scale = 4)
    private BigDecimal electricityKwh;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String fuelInputsJson;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String transportInputsJson;

    @NotNull
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal totalEmissionsKg;

    @NotNull
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal totalEmissionsTonnes;

    @NotNull
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal scope1EmissionsKg;

    @NotNull
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal scope2EmissionsKg;

    @NotNull
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal scope3EmissionsKg;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String fullResultJson;

    @Column(length = 1000)
    private String notes;

    @Column(length = 100)
    private String createdBy;

    public CarbonCalculationRecord() {}

    public Long getId() {
        return id;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public ReportingPeriod getReportingPeriod() {
        return reportingPeriod;
    }

    public void setReportingPeriod(ReportingPeriod reportingPeriod) {
        this.reportingPeriod = reportingPeriod;
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

    public String getFuelInputsJson() {
        return fuelInputsJson;
    }

    public void setFuelInputsJson(String fuelInputsJson) {
        this.fuelInputsJson = fuelInputsJson;
    }

    public String getTransportInputsJson() {
        return transportInputsJson;
    }

    public void setTransportInputsJson(String transportInputsJson) {
        this.transportInputsJson = transportInputsJson;
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

    public String getFullResultJson() {
        return fullResultJson;
    }

    public void setFullResultJson(String fullResultJson) {
        this.fullResultJson = fullResultJson;
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
}
