package com.esg.esg_reporting_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;

@Entity
@Table(name = "metric_entries")
public class MetricEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "metric_definition_id")
    private MetricDefinition metricDefinition;

    @ManyToOne(optional = false)
    @JoinColumn(name = "reporting_period_id")
    private ReportingPeriod reportingPeriod;

    @NotNull
    @DecimalMin("0.0")
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal value;

    @NotBlank
    @Column(nullable = false)
    private String dataStatus; // PROVIDED, ESTIMATED, UNAVAILABLE, NOT_APPLICABLE

    @NotBlank
    @Column(nullable = false)
    private String dataSource;

    @Column(length = 1000)
    private String assumptions;

    public MetricEntry() {}

    public Long getId() { return id; }

    public MetricDefinition getMetricDefinition() {
        return metricDefinition;
    }

    public void setMetricDefinition(MetricDefinition metricDefinition) {
        this.metricDefinition = metricDefinition;
    }

    public ReportingPeriod getReportingPeriod() {
        return reportingPeriod;
    }

    public void setReportingPeriod(ReportingPeriod reportingPeriod) {
        this.reportingPeriod = reportingPeriod;
    }

    public BigDecimal getValue() { return value; }

    public void setValue(BigDecimal value) { this.value = value; }

    public String getDataStatus() { return dataStatus; }

    public void setDataStatus(String dataStatus) {
        this.dataStatus = dataStatus;
    }

    public String getDataSource() { return dataSource; }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getAssumptions() { return assumptions; }

    public void setAssumptions(String assumptions) {
        this.assumptions = assumptions;
    }
}