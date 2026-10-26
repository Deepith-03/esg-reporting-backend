package com.esg.esg_reporting_backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.esg.esg_reporting_backend.dto.MetricEntryRequest;
import com.esg.esg_reporting_backend.entity.MetricEntry;
import com.esg.esg_reporting_backend.service.MetricEntryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/periods/{periodId}/entries")
public class MetricEntryController {

    private final MetricEntryService service;

    public MetricEntryController(MetricEntryService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MetricEntry create(
            @PathVariable Long periodId,
            @Valid @RequestBody MetricEntryRequest request) {
        return service.create(periodId, request);
    }

    @GetMapping
    public List<MetricEntry> getByPeriod(
            @PathVariable Long periodId) {
        return service.getByPeriod(periodId);
    }
}