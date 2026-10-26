package com.esg.esg_reporting_backend.service.carbon;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.esg.esg_reporting_backend.dto.carbon.CarbonCalculationRecordDto;
import com.esg.esg_reporting_backend.dto.carbon.CarbonCalculationRequest;
import com.esg.esg_reporting_backend.dto.carbon.CarbonCalculationResponse;
import com.esg.esg_reporting_backend.dto.carbon.EmissionFactorDto;
import com.esg.esg_reporting_backend.dto.carbon.FuelInputDto;
import com.esg.esg_reporting_backend.dto.carbon.SaveCarbonCalculationRequest;
import com.esg.esg_reporting_backend.dto.carbon.TransportInputDto;
import com.esg.esg_reporting_backend.entity.CarbonCalculationRecord;
import com.esg.esg_reporting_backend.entity.Organization;
import com.esg.esg_reporting_backend.entity.ReportingPeriod;
import com.esg.esg_reporting_backend.repo.CarbonCalculationRepository;
import com.esg.esg_reporting_backend.repo.OrganizationRepository;
import com.esg.esg_reporting_backend.repo.ReportingPeriodRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class CarbonCalculatorService {

    private final SampleLocalCarbonCalculatorProvider localProvider;
    private final ExternalCarbonCalculatorClient externalClient;
    private final CarbonCalculationRepository calculationRepository;
    private final OrganizationRepository organizationRepository;
    private final ReportingPeriodRepository reportingPeriodRepository;
    private final ObjectMapper objectMapper;

    public CarbonCalculatorService(
            SampleLocalCarbonCalculatorProvider localProvider,
            ExternalCarbonCalculatorClient externalClient,
            CarbonCalculationRepository calculationRepository,
            OrganizationRepository organizationRepository,
            ReportingPeriodRepository reportingPeriodRepository,
            ObjectMapper objectMapper) {
        this.localProvider = localProvider;
        this.externalClient = externalClient;
        this.calculationRepository = calculationRepository;
        this.organizationRepository = organizationRepository;
        this.reportingPeriodRepository = reportingPeriodRepository;
        this.objectMapper = objectMapper;
    }

    public CarbonCalculationResponse calculate(CarbonCalculationRequest request) {
        validateCalculationRequest(request);

        // Extensible hook: if external API client is configured with credentials and provider is EXTERNAL,
        // it would delegate here. Otherwise, it uses the local sample factors engine.
        return localProvider.calculate(request);
    }

    public List<EmissionFactorDto> getAvailableFactors() {
        return localProvider.getAvailableFactors();
    }

    public Map<String, Object> getProviderStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("providerId", localProvider.getProviderIdentifier());
        status.put("providerDisplayName", localProvider.getProviderDisplayName());
        status.put("isOfficial", localProvider.isOfficial());
        status.put("disclaimer", SampleLocalCarbonCalculatorProvider.SAMPLE_DISCLAIMER);
        status.put("isExternalApiConfigured", externalClient.isConfigured());
        status.put("externalApiMode", externalClient.getProviderMode());
        status.put("externalApiUrl", externalClient.getApiUrl());
        return status;
    }

    @Transactional
    public CarbonCalculationRecordDto saveCalculation(SaveCarbonCalculationRequest saveRequest, String username) {
        if (saveRequest == null) {
            throw new IllegalArgumentException("Save request cannot be null");
        }
        if (saveRequest.getOrganizationId() == null) {
            throw new IllegalArgumentException("Organization ID is required");
        }
        if (saveRequest.getTitle() == null || saveRequest.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Calculation title is required");
        }

        Organization org = organizationRepository.findById(saveRequest.getOrganizationId())
                .orElseThrow(() -> new IllegalArgumentException("Organization not found with ID: " + saveRequest.getOrganizationId()));

        ReportingPeriod period = null;
        if (saveRequest.getReportingPeriodId() != null) {
            period = reportingPeriodRepository.findById(saveRequest.getReportingPeriodId())
                    .orElseThrow(() -> new IllegalArgumentException("Reporting period not found with ID: " + saveRequest.getReportingPeriodId()));

            if (!period.getOrganization().getId().equals(org.getId())) {
                throw new IllegalArgumentException("Reporting period '" + period.getName() + "' does not belong to organization '" + org.getName() + "'");
            }
        }

        CarbonCalculationResponse response = calculate(saveRequest.getCalculationRequest());

        CarbonCalculationRecord record = new CarbonCalculationRecord();
        record.setOrganization(org);
        record.setReportingPeriod(period);
        record.setTitle(saveRequest.getTitle().trim());
        record.setCalculatedAt(LocalDateTime.now());
        record.setCalculationMethod(response.getCalculationMethod());
        record.setElectricityKwh(saveRequest.getCalculationRequest().getElectricityKwh());

        try {
            record.setFuelInputsJson(objectMapper.writeValueAsString(saveRequest.getCalculationRequest().getFuelEntries()));
            record.setTransportInputsJson(objectMapper.writeValueAsString(saveRequest.getCalculationRequest().getTransportEntries()));
            record.setFullResultJson(objectMapper.writeValueAsString(response));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize calculation snapshot", e);
        }

        record.setTotalEmissionsKg(response.getTotalEmissionsKg());
        record.setTotalEmissionsTonnes(response.getTotalEmissionsTonnes());
        record.setScope1EmissionsKg(response.getScope1EmissionsKg());
        record.setScope2EmissionsKg(response.getScope2EmissionsKg());
        record.setScope3EmissionsKg(response.getScope3EmissionsKg());
        record.setNotes(saveRequest.getNotes());
        record.setCreatedBy(username != null ? username : "system");

        CarbonCalculationRecord saved = calculationRepository.save(record);
        return mapToDto(saved, response);
    }

    @Transactional(readOnly = true)
    public List<CarbonCalculationRecordDto> getCalculationHistory(Long organizationId, Long reportingPeriodId) {
        List<CarbonCalculationRecord> records;

        if (organizationId != null && reportingPeriodId != null) {
            records = calculationRepository.findByOrganizationIdAndReportingPeriodIdOrderByCalculatedAtDesc(organizationId, reportingPeriodId);
        } else if (organizationId != null) {
            records = calculationRepository.findByOrganizationIdOrderByCalculatedAtDesc(organizationId);
        } else if (reportingPeriodId != null) {
            records = calculationRepository.findByReportingPeriodIdOrderByCalculatedAtDesc(reportingPeriodId);
        } else {
            records = calculationRepository.findAllByOrderByCalculatedAtDesc();
        }

        return records.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CarbonCalculationRecordDto getCalculationById(Long id) {
        CarbonCalculationRecord record = calculationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Carbon calculation record not found with ID: " + id));
        return mapToDto(record);
    }

    @Transactional
    public void deleteCalculation(Long id) {
        if (!calculationRepository.existsById(id)) {
            throw new IllegalArgumentException("Carbon calculation record not found with ID: " + id);
        }
        calculationRepository.deleteById(id);
    }

    private void validateCalculationRequest(CarbonCalculationRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Calculation request cannot be null");
        }

        boolean hasElectricity = request.getElectricityKwh() != null && request.getElectricityKwh().compareTo(BigDecimal.ZERO) > 0;
        boolean hasFuel = false;
        boolean hasTransport = false;

        if (request.getElectricityKwh() != null && request.getElectricityKwh().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Electricity consumption cannot be negative");
        }

        if (request.getFuelEntries() != null) {
            for (FuelInputDto f : request.getFuelEntries()) {
                if (f == null) continue;
                if (f.getQuantity() != null && f.getQuantity().compareTo(BigDecimal.ZERO) < 0) {
                    throw new IllegalArgumentException("Fuel quantity cannot be negative");
                }
                if (f.getQuantity() != null && f.getQuantity().compareTo(BigDecimal.ZERO) > 0) {
                    hasFuel = true;
                }
            }
        }

        if (request.getTransportEntries() != null) {
            for (TransportInputDto t : request.getTransportEntries()) {
                if (t == null) continue;
                if (t.getDistance() != null && t.getDistance().compareTo(BigDecimal.ZERO) < 0) {
                    throw new IllegalArgumentException("Transport distance cannot be negative");
                }
                if (t.getDistance() != null && t.getDistance().compareTo(BigDecimal.ZERO) > 0) {
                    hasTransport = true;
                }
            }
        }

        if (!hasElectricity && !hasFuel && !hasTransport) {
            throw new IllegalArgumentException("At least one activity (electricity, fuel, or transportation) must be provided with a value greater than zero.");
        }
    }

    private CarbonCalculationRecordDto mapToDto(CarbonCalculationRecord record) {
        return mapToDto(record, null);
    }

    private CarbonCalculationRecordDto mapToDto(CarbonCalculationRecord record, CarbonCalculationResponse knownResponse) {
        CarbonCalculationRecordDto dto = new CarbonCalculationRecordDto();
        dto.setId(record.getId());
        dto.setOrganizationId(record.getOrganization().getId());
        dto.setOrganizationName(record.getOrganization().getName());

        if (record.getReportingPeriod() != null) {
            dto.setReportingPeriodId(record.getReportingPeriod().getId());
            dto.setReportingPeriodName(record.getReportingPeriod().getName());
        }

        dto.setTitle(record.getTitle());
        dto.setCalculatedAt(record.getCalculatedAt());
        dto.setCalculationMethod(record.getCalculationMethod());
        dto.setElectricityKwh(record.getElectricityKwh());
        dto.setTotalEmissionsKg(record.getTotalEmissionsKg());
        dto.setTotalEmissionsTonnes(record.getTotalEmissionsTonnes());
        dto.setScope1EmissionsKg(record.getScope1EmissionsKg());
        dto.setScope2EmissionsKg(record.getScope2EmissionsKg());
        dto.setScope3EmissionsKg(record.getScope3EmissionsKg());
        dto.setNotes(record.getNotes());
        dto.setCreatedBy(record.getCreatedBy());

        if (knownResponse != null) {
            dto.setFullResult(knownResponse);
        } else if (record.getFullResultJson() != null && !record.getFullResultJson().isEmpty()) {
            try {
                CarbonCalculationResponse parsed = objectMapper.readValue(record.getFullResultJson(), CarbonCalculationResponse.class);
                dto.setFullResult(parsed);
            } catch (Exception e) {
                // Return dto without full breakdown if json parsing fails
            }
        }

        return dto;
    }
}
