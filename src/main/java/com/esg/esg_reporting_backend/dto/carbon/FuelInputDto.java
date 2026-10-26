package com.esg.esg_reporting_backend.dto.carbon;

import java.math.BigDecimal;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class FuelInputDto {

    @NotBlank(message = "Fuel type is required")
    private String fuelType;

    @NotNull(message = "Fuel quantity is required")
    @DecimalMin(value = "0.0", message = "Fuel quantity cannot be negative")
    private BigDecimal quantity;

    @NotBlank(message = "Fuel unit is required")
    private String unit;

    public FuelInputDto() {}

    public FuelInputDto(String fuelType, BigDecimal quantity, String unit) {
        this.fuelType = fuelType;
        this.quantity = quantity;
        this.unit = unit;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
