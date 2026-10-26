package com.esg.esg_reporting_backend.service.carbon;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mockito;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.esg.esg_reporting_backend.dto.carbon.CarbonCalculationRecordDto;
import com.esg.esg_reporting_backend.dto.carbon.CarbonCalculationRequest;
import com.esg.esg_reporting_backend.dto.carbon.FuelInputDto;
import com.esg.esg_reporting_backend.dto.carbon.SaveCarbonCalculationRequest;
import com.esg.esg_reporting_backend.entity.CarbonCalculationRecord;
import com.esg.esg_reporting_backend.entity.Organization;
import com.esg.esg_reporting_backend.entity.ReportingPeriod;
import com.esg.esg_reporting_backend.repo.CarbonCalculationRepository;
import com.esg.esg_reporting_backend.repo.OrganizationRepository;
import com.esg.esg_reporting_backend.repo.ReportingPeriodRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

class CarbonCalculatorServiceTest {

    private SampleLocalCarbonCalculatorProvider localProvider;
    private ExternalCarbonCalculatorClient externalClient;
    private CarbonCalculationRepository calculationRepository;
    private OrganizationRepository organizationRepository;
    private ReportingPeriodRepository reportingPeriodRepository;
    private ObjectMapper objectMapper;
    private CarbonCalculatorService service;

    @BeforeEach
    void setUp() {
        localProvider = new SampleLocalCarbonCalculatorProvider("0.420000");
        externalClient = Mockito.mock(ExternalCarbonCalculatorClient.class);
        calculationRepository = Mockito.mock(CarbonCalculationRepository.class);
        organizationRepository = Mockito.mock(OrganizationRepository.class);
        reportingPeriodRepository = Mockito.mock(ReportingPeriodRepository.class);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

        service = new CarbonCalculatorService(
                localProvider,
                externalClient,
                calculationRepository,
                organizationRepository,
                reportingPeriodRepository,
                objectMapper
        );
    }

    @Test
    @DisplayName("Should throw exception when all activity values are empty or zero")
    void shouldRejectAllZeroActivities() {
        CarbonCalculationRequest emptyRequest = new CarbonCalculationRequest();
        emptyRequest.setElectricityKwh(BigDecimal.ZERO);
        assertThrows(IllegalArgumentException.class, () -> service.calculate(emptyRequest));
    }

    @Test
    @DisplayName("Should save calculation record with organization and reporting period association")
    void shouldSaveCalculationSuccessfully() {
        Organization org = new Organization("Acme Corp", "Manufacturing");
        try {
            var idField = Organization.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(org, 1L);
        } catch (Exception ignored) {}

        ReportingPeriod period = new ReportingPeriod();
        period.setName("FY 2025");
        period.setOrganization(org);
        try {
            var idField = ReportingPeriod.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(period, 10L);
        } catch (Exception ignored) {}

        when(organizationRepository.findById(1L)).thenReturn(Optional.of(org));
        when(reportingPeriodRepository.findById(10L)).thenReturn(Optional.of(period));

        when(calculationRepository.save(any(CarbonCalculationRecord.class))).thenAnswer(invocation -> {
            CarbonCalculationRecord r = invocation.getArgument(0);
            try {
                var idField = CarbonCalculationRecord.class.getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(r, 100L);
            } catch (Exception ignored) {}
            return r;
        });

        SaveCarbonCalculationRequest saveReq = new SaveCarbonCalculationRequest();
        saveReq.setOrganizationId(1L);
        saveReq.setReportingPeriodId(10L);
        saveReq.setTitle("Q1 Facility Footprint");
        saveReq.setNotes("Assessed based on utility bills");

        CarbonCalculationRequest calcReq = new CarbonCalculationRequest();
        calcReq.setElectricityKwh(new BigDecimal("2000.00"));
        calcReq.setFuelEntries(List.of(new FuelInputDto("DIESEL", new BigDecimal("150.00"), "LITERS")));
        saveReq.setCalculationRequest(calcReq);

        CarbonCalculationRecordDto savedDto = service.saveCalculation(saveReq, "dataentry");

        assertNotNull(savedDto);
        assertEquals(100L, savedDto.getId());
        assertEquals("Acme Corp", savedDto.getOrganizationName());
        assertEquals("FY 2025", savedDto.getReportingPeriodName());
        assertEquals("dataentry", savedDto.getCreatedBy());
        assertNotNull(savedDto.getFullResult());
        verify(calculationRepository).save(any(CarbonCalculationRecord.class));
    }

    @Test
    @DisplayName("Should reject saving calculation if reporting period belongs to different organization")
    void shouldRejectMismatchedOrganizationAndPeriod() {
        Organization org1 = new Organization("Org 1", "Tech");
        Organization org2 = new Organization("Org 2", "Retail");
        try {
            var idField = Organization.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(org1, 1L);
            idField.set(org2, 2L);
        } catch (Exception ignored) {}

        ReportingPeriod period = new ReportingPeriod();
        period.setName("Period Org 2");
        period.setOrganization(org2);

        when(organizationRepository.findById(1L)).thenReturn(Optional.of(org1));
        when(reportingPeriodRepository.findById(20L)).thenReturn(Optional.of(period));

        SaveCarbonCalculationRequest saveReq = new SaveCarbonCalculationRequest();
        saveReq.setOrganizationId(1L);
        saveReq.setReportingPeriodId(20L);
        saveReq.setTitle("Mismatched Calculation");

        CarbonCalculationRequest calcReq = new CarbonCalculationRequest();
        calcReq.setElectricityKwh(new BigDecimal("100.00"));
        saveReq.setCalculationRequest(calcReq);

        assertThrows(IllegalArgumentException.class, () -> service.saveCalculation(saveReq, "admin"));
    }
}
