package com.esg.esg_reporting_backend.controller;

import java.util.List;
import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.esg.esg_reporting_backend.entity.MetricDefinition;
import com.esg.esg_reporting_backend.repo.MetricDefinitionRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/metric-definitions")
public class MetricDefinitionController {

    private final MetricDefinitionRepository repository;

    public MetricDefinitionController(MetricDefinitionRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MetricDefinition create(@Valid @RequestBody MetricDefinition definition) {

        String pillar = definition.getPillar()
                .toUpperCase(Locale.ROOT);

        if (!List.of("ENVIRONMENTAL", "SOCIAL", "GOVERNANCE")
                .contains(pillar)) {
            throw new IllegalArgumentException(
                    "Invalid ESG pillar");
        }

        definition.setPillar(pillar);
        return repository.save(definition);
    }

    @GetMapping
    public List<MetricDefinition> getAll(
            @RequestParam(required = false) String pillar) {

        if (pillar != null && !pillar.isBlank()) {
            String normalized = pillar.toUpperCase(Locale.ROOT);

            if (!List.of("ENVIRONMENTAL", "SOCIAL", "GOVERNANCE")
                    .contains(normalized)) {
                throw new IllegalArgumentException(
                        "Invalid ESG pillar");
            }

            return repository.findByPillarIgnoreCase(normalized);
        }

        return repository.findAll();
    }
}