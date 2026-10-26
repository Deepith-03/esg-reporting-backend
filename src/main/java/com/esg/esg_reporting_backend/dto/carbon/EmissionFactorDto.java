package com.esg.esg_reporting_backend.dto.carbon;

import java.math.BigDecimal;

public class EmissionFactorDto {

    private String category; // ELECTRICITY, FUEL, TRANSPORT
    private String typeKey; // e.g. DIESEL, PETROL, GRID_ELECTRICITY
    private String displayName; // e.g. "Diesel Fuel", "Passenger Car (Petrol)"
    private String unit; // e.g. "LITERS", "KWH", "KM"
    private BigDecimal factorKgCo2ePerUnit;
    private String factorUnit; // e.g. "kg CO2e / liter"
    private String scope; // SCOPE_1, SCOPE_2, SCOPE_3
    private String sourceCitation;
    private boolean isSampleFactor;
    private String notes;

    public EmissionFactorDto() {}

    public EmissionFactorDto(
            String category,
            String typeKey,
            String displayName,
            String unit,
            BigDecimal factorKgCo2ePerUnit,
            String factorUnit,
            String scope,
            String sourceCitation,
            boolean isSampleFactor,
            String notes) {
        this.category = category;
        this.typeKey = typeKey;
        this.displayName = displayName;
        this.unit = unit;
        this.factorKgCo2ePerUnit = factorKgCo2ePerUnit;
        this.factorUnit = factorUnit;
        this.scope = scope;
        this.sourceCitation = sourceCitation;
        this.isSampleFactor = isSampleFactor;
        this.notes = notes;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTypeKey() {
        return typeKey;
    }

    public void setTypeKey(String typeKey) {
        this.typeKey = typeKey;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public BigDecimal getFactorKgCo2ePerUnit() {
        return factorKgCo2ePerUnit;
    }

    public void setFactorKgCo2ePerUnit(BigDecimal factorKgCo2ePerUnit) {
        this.factorKgCo2ePerUnit = factorKgCo2ePerUnit;
    }

    public String getFactorUnit() {
        return factorUnit;
    }

    public void setFactorUnit(String factorUnit) {
        this.factorUnit = factorUnit;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getSourceCitation() {
        return sourceCitation;
    }

    public void setSourceCitation(String sourceCitation) {
        this.sourceCitation = sourceCitation;
    }

    public boolean isSampleFactor() {
        return isSampleFactor;
    }

    public void setSampleFactor(boolean sampleFactor) {
        isSampleFactor = sampleFactor;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
