package com.esg.esg_reporting_backend.dto.carbon;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CarbonCalculationResponse {

    private String calculationMethod; // e.g. SAMPLE_LOCAL_CALCULATION
    private boolean isOfficial;
    private String disclaimer;
    private String providerName;
    private LocalDateTime calculatedAt;

    private ElectricityCalculationResultDto electricity;
    private List<FuelCalculationResultDto> fuelItems = new ArrayList<>();
    private List<TransportCalculationResultDto> transportItems = new ArrayList<>();

    // Totals by Category
    private BigDecimal totalElectricityEmissionsKg;
    private BigDecimal totalElectricityEmissionsTonnes;
    private BigDecimal totalFuelEmissionsKg;
    private BigDecimal totalFuelEmissionsTonnes;
    private BigDecimal totalTransportEmissionsKg;
    private BigDecimal totalTransportEmissionsTonnes;

    // GHG Protocol Scopes
    private BigDecimal scope1EmissionsKg;
    private BigDecimal scope1EmissionsTonnes;
    private BigDecimal scope2EmissionsKg;
    private BigDecimal scope2EmissionsTonnes;
    private BigDecimal scope3EmissionsKg;
    private BigDecimal scope3EmissionsTonnes;

    // Overall Totals
    private BigDecimal totalEmissionsKg;
    private BigDecimal totalEmissionsTonnes;

    // Scope percentage breakdown (0 - 100)
    private BigDecimal scope1Percentage;
    private BigDecimal scope2Percentage;
    private BigDecimal scope3Percentage;

    public CarbonCalculationResponse() {}

    public String getCalculationMethod() {
        return calculationMethod;
    }

    public void setCalculationMethod(String calculationMethod) {
        this.calculationMethod = calculationMethod;
    }

    public boolean isOfficial() {
        return isOfficial;
    }

    public void setOfficial(boolean official) {
        isOfficial = official;
    }

    public String getDisclaimer() {
        return disclaimer;
    }

    public void setDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public LocalDateTime getCalculatedAt() {
        return calculatedAt;
    }

    public void setCalculatedAt(LocalDateTime calculatedAt) {
        this.calculatedAt = calculatedAt;
    }

    public ElectricityCalculationResultDto getElectricity() {
        return electricity;
    }

    public void setElectricity(ElectricityCalculationResultDto electricity) {
        this.electricity = electricity;
    }

    public List<FuelCalculationResultDto> getFuelItems() {
        return fuelItems;
    }

    public void setFuelItems(List<FuelCalculationResultDto> fuelItems) {
        this.fuelItems = fuelItems != null ? fuelItems : new ArrayList<>();
    }

    public List<TransportCalculationResultDto> getTransportItems() {
        return transportItems;
    }

    public void setTransportItems(List<TransportCalculationResultDto> transportItems) {
        this.transportItems = transportItems != null ? transportItems : new ArrayList<>();
    }

    public BigDecimal getTotalElectricityEmissionsKg() {
        return totalElectricityEmissionsKg;
    }

    public void setTotalElectricityEmissionsKg(BigDecimal totalElectricityEmissionsKg) {
        this.totalElectricityEmissionsKg = totalElectricityEmissionsKg;
    }

    public BigDecimal getTotalElectricityEmissionsTonnes() {
        return totalElectricityEmissionsTonnes;
    }

    public void setTotalElectricityEmissionsTonnes(BigDecimal totalElectricityEmissionsTonnes) {
        this.totalElectricityEmissionsTonnes = totalElectricityEmissionsTonnes;
    }

    public BigDecimal getTotalFuelEmissionsKg() {
        return totalFuelEmissionsKg;
    }

    public void setTotalFuelEmissionsKg(BigDecimal totalFuelEmissionsKg) {
        this.totalFuelEmissionsKg = totalFuelEmissionsKg;
    }

    public BigDecimal getTotalFuelEmissionsTonnes() {
        return totalFuelEmissionsTonnes;
    }

    public void setTotalFuelEmissionsTonnes(BigDecimal totalFuelEmissionsTonnes) {
        this.totalFuelEmissionsTonnes = totalFuelEmissionsTonnes;
    }

    public BigDecimal getTotalTransportEmissionsKg() {
        return totalTransportEmissionsKg;
    }

    public void setTotalTransportEmissionsKg(BigDecimal totalTransportEmissionsKg) {
        this.totalTransportEmissionsKg = totalTransportEmissionsKg;
    }

    public BigDecimal getTotalTransportEmissionsTonnes() {
        return totalTransportEmissionsTonnes;
    }

    public void setTotalTransportEmissionsTonnes(BigDecimal totalTransportEmissionsTonnes) {
        this.totalTransportEmissionsTonnes = totalTransportEmissionsTonnes;
    }

    public BigDecimal getScope1EmissionsKg() {
        return scope1EmissionsKg;
    }

    public void setScope1EmissionsKg(BigDecimal scope1EmissionsKg) {
        this.scope1EmissionsKg = scope1EmissionsKg;
    }

    public BigDecimal getScope1EmissionsTonnes() {
        return scope1EmissionsTonnes;
    }

    public void setScope1EmissionsTonnes(BigDecimal scope1EmissionsTonnes) {
        this.scope1EmissionsTonnes = scope1EmissionsTonnes;
    }

    public BigDecimal getScope2EmissionsKg() {
        return scope2EmissionsKg;
    }

    public void setScope2EmissionsKg(BigDecimal scope2EmissionsKg) {
        this.scope2EmissionsKg = scope2EmissionsKg;
    }

    public BigDecimal getScope2EmissionsTonnes() {
        return scope2EmissionsTonnes;
    }

    public void setScope2EmissionsTonnes(BigDecimal scope2EmissionsTonnes) {
        this.scope2EmissionsTonnes = scope2EmissionsTonnes;
    }

    public BigDecimal getScope3EmissionsKg() {
        return scope3EmissionsKg;
    }

    public void setScope3EmissionsKg(BigDecimal scope3EmissionsKg) {
        this.scope3EmissionsKg = scope3EmissionsKg;
    }

    public BigDecimal getScope3EmissionsTonnes() {
        return scope3EmissionsTonnes;
    }

    public void setScope3EmissionsTonnes(BigDecimal scope3EmissionsTonnes) {
        this.scope3EmissionsTonnes = scope3EmissionsTonnes;
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

    public BigDecimal getScope1Percentage() {
        return scope1Percentage;
    }

    public void setScope1Percentage(BigDecimal scope1Percentage) {
        this.scope1Percentage = scope1Percentage;
    }

    public BigDecimal getScope2Percentage() {
        return scope2Percentage;
    }

    public void setScope2Percentage(BigDecimal scope2Percentage) {
        this.scope2Percentage = scope2Percentage;
    }

    public BigDecimal getScope3Percentage() {
        return scope3Percentage;
    }

    public void setScope3Percentage(BigDecimal scope3Percentage) {
        this.scope3Percentage = scope3Percentage;
    }
}
