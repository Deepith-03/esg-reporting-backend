package com.esg.esg_reporting_backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record MetricEntryRequest(
        @NotNull Long metricDefinitionId,
        @NotNull @DecimalMin("0.0") BigDecimal value,
        @NotBlank String dataStatus,
        @NotBlank String dataSource,
        String assumptions
) {}