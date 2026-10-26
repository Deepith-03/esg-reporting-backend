package com.esg.esg_reporting_backend.dto.carbon;

import java.math.BigDecimal;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TransportInputDto {

    @NotBlank(message = "Transport type is required")
    private String transportType;

    @NotNull(message = "Transport distance is required")
    @DecimalMin(value = "0.0", message = "Transport distance cannot be negative")
    private BigDecimal distance;

    @NotBlank(message = "Distance unit is required")
    private String unit; // KM or MILES

    public TransportInputDto() {}

    public TransportInputDto(String transportType, BigDecimal distance, String unit) {
        this.transportType = transportType;
        this.distance = distance;
        this.unit = unit;
    }

    public String getTransportType() {
        return transportType;
    }

    public void setTransportType(String transportType) {
        this.transportType = transportType;
    }

    public BigDecimal getDistance() {
        return distance;
    }

    public void setDistance(BigDecimal distance) {
        this.distance = distance;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
