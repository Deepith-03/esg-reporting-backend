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

import com.esg.esg_reporting_backend.dto.ReportingPeriodRequest;
import com.esg.esg_reporting_backend.entity.ReportingPeriod;
import com.esg.esg_reporting_backend.service.ReportingPeriodService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/organizations/{organizationId}/periods")
public class ReportingPeriodController {

    private final ReportingPeriodService service;

    public ReportingPeriodController(
            ReportingPeriodService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReportingPeriod create(@PathVariable Long organizationId,@Valid @RequestBody ReportingPeriodRequest request) {
        return service.create(organizationId, request);
    }

    @GetMapping
    public List<ReportingPeriod> getAll(@PathVariable Long organizationId) {
        return service.findByOrganization(organizationId);
    }
}