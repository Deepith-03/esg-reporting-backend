package com.esg.esg_reporting_backend.controller;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.esg.esg_reporting_backend.dto.carbon.CarbonCalculationRecordDto;
import com.esg.esg_reporting_backend.dto.carbon.CarbonCalculationResponse;
import com.esg.esg_reporting_backend.dto.carbon.SaveCarbonCalculationRequest;
import com.esg.esg_reporting_backend.service.carbon.CarbonCalculatorService;

@SpringBootTest
class CarbonCalculatorControllerSecurityTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockitoBean
    private CarbonCalculatorService calculatorService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("Unauthenticated request to carbon calculator should return 401 Unauthorized")
    void unauthenticatedRequestShouldFail() throws Exception {
        mockMvc.perform(get("/api/carbon-calculator/factors"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "viewer", roles = {"VIEWER"})
    @DisplayName("VIEWER role can access factors and preview calculation")
    void viewerCanCalculate() throws Exception {
        when(calculatorService.getAvailableFactors()).thenReturn(List.of());

        mockMvc.perform(get("/api/carbon-calculator/factors"))
                .andExpect(status().isOk());

        CarbonCalculationResponse dummyResponse = new CarbonCalculationResponse();
        dummyResponse.setTotalEmissionsKg(new BigDecimal("100.00"));
        when(calculatorService.calculate(any())).thenReturn(dummyResponse);

        String calcJson = """
            {
                "electricityKwh": 200.0
            }
            """;

        mockMvc.perform(post("/api/carbon-calculator/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(calcJson))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "viewer", roles = {"VIEWER"})
    @DisplayName("VIEWER role is forbidden from saving calculation results")
    void viewerCannotSave() throws Exception {
        String saveJson = """
            {
                "organizationId": 1,
                "title": "Facility Footprint",
                "calculationRequest": {
                    "electricityKwh": 100.0
                }
            }
            """;

        mockMvc.perform(post("/api/carbon-calculator/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(saveJson))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "dataentry", roles = {"DATA_ENTRY"})
    @DisplayName("DATA_ENTRY role can save calculation results")
    void dataEntryCanSave() throws Exception {
        CarbonCalculationRecordDto savedDto = new CarbonCalculationRecordDto();
        savedDto.setId(50L);
        savedDto.setTitle("Q2 Facility Carbon");
        when(calculatorService.saveCalculation(any(SaveCarbonCalculationRequest.class), anyString())).thenReturn(savedDto);

        String saveJson = """
            {
                "organizationId": 1,
                "title": "Q2 Facility Carbon",
                "calculationRequest": {
                    "electricityKwh": 100.0
                }
            }
            """;

        mockMvc.perform(post("/api/carbon-calculator/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(saveJson))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "dataentry", roles = {"DATA_ENTRY"})
    @DisplayName("DATA_ENTRY role cannot delete calculations")
    void dataEntryCannotDelete() throws Exception {
        mockMvc.perform(delete("/api/carbon-calculator/history/50"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("ADMIN role can delete calculations")
    void adminCanDelete() throws Exception {
        mockMvc.perform(delete("/api/carbon-calculator/history/50"))
                .andExpect(status().isOk());
    }
}
