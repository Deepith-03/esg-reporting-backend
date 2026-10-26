package com.esg.esg_reporting_backend.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.esg.esg_reporting_backend.dto.carbon.CarbonCalculationRecordDto;
import com.esg.esg_reporting_backend.dto.carbon.CarbonCalculationRequest;
import com.esg.esg_reporting_backend.dto.carbon.CarbonCalculationResponse;
import com.esg.esg_reporting_backend.dto.carbon.EmissionFactorDto;
import com.esg.esg_reporting_backend.dto.carbon.SaveCarbonCalculationRequest;
import com.esg.esg_reporting_backend.service.carbon.CarbonCalculatorService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/carbon-calculator")
public class CarbonCalculatorController {

    private final CarbonCalculatorService calculatorService;

    public CarbonCalculatorController(CarbonCalculatorService calculatorService) {
        this.calculatorService = calculatorService;
    }

    @GetMapping("/factors")
    public List<EmissionFactorDto> getAvailableFactors() {
        return calculatorService.getAvailableFactors();
    }

    @GetMapping("/provider-status")
    public Map<String, Object> getProviderStatus() {
        return calculatorService.getProviderStatus();
    }

    @PostMapping("/calculate")
    public ResponseEntity<CarbonCalculationResponse> calculate(
            @Valid @RequestBody CarbonCalculationRequest request) {
        CarbonCalculationResponse response = calculatorService.calculate(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/save")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CarbonCalculationRecordDto> saveCalculation(
            @Valid @RequestBody SaveCarbonCalculationRequest saveRequest,
            Principal principal) {
        String username = principal != null ? principal.getName() : "anonymous";
        CarbonCalculationRecordDto saved = calculatorService.saveCalculation(saveRequest, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/history")
    public List<CarbonCalculationRecordDto> getCalculationHistory(
            @RequestParam(required = false) Long organizationId,
            @RequestParam(required = false) Long reportingPeriodId) {
        return calculatorService.getCalculationHistory(organizationId, reportingPeriodId);
    }

    @GetMapping("/history/{id}")
    public ResponseEntity<CarbonCalculationRecordDto> getCalculationById(@PathVariable Long id) {
        return ResponseEntity.ok(calculatorService.getCalculationById(id));
    }

    @DeleteMapping("/history/{id}")
    public ResponseEntity<Map<String, Object>> deleteCalculation(@PathVariable Long id) {
        calculatorService.deleteCalculation(id);
        return ResponseEntity.ok(Map.of("success", true, "message", "Calculation record deleted successfully"));
    }
}
