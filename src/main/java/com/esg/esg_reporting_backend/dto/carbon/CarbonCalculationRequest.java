package com.esg.esg_reporting_backend.dto.carbon;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;

public class CarbonCalculationRequest {

    @DecimalMin(value = "0.0", message = "Electricity consumption cannot be negative")
    private BigDecimal electricityKwh;

    @Valid
    private List<FuelInputDto> fuelEntries = new ArrayList<>();

    @Valid
    private List<TransportInputDto> transportEntries = new ArrayList<>();

    public CarbonCalculationRequest() {}

    public CarbonCalculationRequest(BigDecimal electricityKwh, List<FuelInputDto> fuelEntries, List<TransportInputDto> transportEntries) {
        this.electricityKwh = electricityKwh;
        this.fuelEntries = fuelEntries != null ? fuelEntries : new ArrayList<>();
        this.transportEntries = transportEntries != null ? transportEntries : new ArrayList<>();
    }

    public BigDecimal getElectricityKwh() {
        return electricityKwh;
    }

    public void setElectricityKwh(BigDecimal electricityKwh) {
        this.electricityKwh = electricityKwh;
    }

    public List<FuelInputDto> getFuelEntries() {
        return fuelEntries;
    }

    public void setFuelEntries(List<FuelInputDto> fuelEntries) {
        this.fuelEntries = fuelEntries != null ? fuelEntries : new ArrayList<>();
    }

    public List<TransportInputDto> getTransportEntries() {
        return transportEntries;
    }

    public void setTransportEntries(List<TransportInputDto> transportEntries) {
        this.transportEntries = transportEntries != null ? transportEntries : new ArrayList<>();
    }
}
