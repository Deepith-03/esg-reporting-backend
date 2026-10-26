package com.esg.esg_reporting_backend.dto.carbon;

import java.math.BigDecimal;

public class ElectricityCalculationResultDto {

    private BigDecimal kwh;
    private BigDecimal factorUsed;
    private String factorUnit;
    private String factorSource;
    private BigDecimal emissionsKg;
    private BigDecimal emissionsTonnes;
    private String scope;
    private String formula;

    public ElectricityCalculationResultDto() {}

    public ElectricityCalculationResultDto(
            BigDecimal kwh,
            BigDecimal factorUsed,
            String factorUnit,
            String factorSource,
            BigDecimal emissionsKg,
            BigDecimal emissionsTonnes,
            String scope,
            String formula) {
        this.kwh = kwh;
        this.factorUsed = factorUsed;
        this.factorUnit = factorUnit;
        this.factorSource = factorSource;
        this.emissionsKg = emissionsKg;
        this.emissionsTonnes = emissionsTonnes;
        this.scope = scope;
        this.formula = formula;
    }

    public BigDecimal getKwh() {
        return kwh;
    }

    public void setKwh(BigDecimal kwh) {
        this.kwh = kwh;
    }

    public BigDecimal getFactorUsed() {
        return factorUsed;
    }

    public void setFactorUsed(BigDecimal factorUsed) {
        this.factorUsed = factorUsed;
    }

    public String getFactorUnit() {
        return factorUnit;
    }

    public void setFactorUnit(String factorUnit) {
        this.factorUnit = factorUnit;
    }

    public String getFactorSource() {
        return factorSource;
    }

    public void setFactorSource(String factorSource) {
        this.factorSource = factorSource;
    }

    public BigDecimal getEmissionsKg() {
        return emissionsKg;
    }

    public void setEmissionsKg(BigDecimal emissionsKg) {
        this.emissionsKg = emissionsKg;
    }

    public BigDecimal getEmissionsTonnes() {
        return emissionsTonnes;
    }

    public void setEmissionsTonnes(BigDecimal emissionsTonnes) {
        this.emissionsTonnes = emissionsTonnes;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }
}
