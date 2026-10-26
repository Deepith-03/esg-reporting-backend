package com.esg.esg_reporting_backend.service.carbon;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.esg.esg_reporting_backend.dto.carbon.CarbonCalculationRequest;
import com.esg.esg_reporting_backend.dto.carbon.CarbonCalculationResponse;
import com.esg.esg_reporting_backend.dto.carbon.EmissionFactorDto;
import com.esg.esg_reporting_backend.dto.carbon.FuelInputDto;
import com.esg.esg_reporting_backend.dto.carbon.TransportInputDto;

class SampleLocalCarbonCalculatorProviderTest {

    private SampleLocalCarbonCalculatorProvider provider;

    @BeforeEach
    void setUp() {
        provider = new SampleLocalCarbonCalculatorProvider("0.420000");
    }

    @Test
    @DisplayName("Should return transparent list of available sample emission factors")
    void shouldReturnAvailableFactors() {
        List<EmissionFactorDto> factors = provider.getAvailableFactors();
        assertNotNull(factors);
        assertTrue(factors.size() >= 15);

        for (EmissionFactorDto f : factors) {
            assertTrue(f.isSampleFactor(), "All dev factors must be marked as sample factors");
            assertNotNull(f.getSourceCitation(), "Each factor must include source citation");
            assertNotNull(f.getFactorKgCo2ePerUnit(), "Each factor must have non-null value");
            assertTrue(f.getFactorKgCo2ePerUnit().compareTo(BigDecimal.ZERO) > 0);
        }
    }

    @Test
    @DisplayName("Should calculate electricity emissions with Scope 2 and transparent formula")
    void shouldCalculateElectricityEmissions() {
        CarbonCalculationRequest request = new CarbonCalculationRequest();
        request.setElectricityKwh(new BigDecimal("1000.00"));

        CarbonCalculationResponse response = provider.calculate(request);

        assertNotNull(response);
        assertFalse(response.isOfficial(), "Sample calculation must not claim official status");
        assertEquals("SAMPLE_LOCAL_CALCULATION", response.getCalculationMethod());
        assertNotNull(response.getDisclaimer());

        // 1000 kWh * 0.420000 = 420.00 kg CO2e = 0.4200 tonnes CO2e
        assertEquals(new BigDecimal("420.00"), response.getElectricity().getEmissionsKg());
        assertEquals(new BigDecimal("0.4200"), response.getElectricity().getEmissionsTonnes());
        assertEquals(new BigDecimal("420.00"), response.getScope2EmissionsKg());
        assertEquals(new BigDecimal("420.00"), response.getTotalEmissionsKg());
        assertEquals(new BigDecimal("0.4200"), response.getTotalEmissionsTonnes());
        assertTrue(response.getElectricity().getFormula().contains("1000.00 kWh * 0.420000 kg CO2e/kWh"));
    }

    @Test
    @DisplayName("Should calculate fuel emissions with Scope 1")
    void shouldCalculateFuelEmissions() {
        CarbonCalculationRequest request = new CarbonCalculationRequest();
        request.setFuelEntries(List.of(
                new FuelInputDto("DIESEL", new BigDecimal("100.00"), "LITERS"),
                new FuelInputDto("PETROL", new BigDecimal("50.00"), "LITERS"),
                new FuelInputDto("NATURAL_GAS", new BigDecimal("500.00"), "KWH")
        ));

        CarbonCalculationResponse response = provider.calculate(request);

        assertNotNull(response);
        assertEquals(3, response.getFuelItems().size());

        // Diesel: 100 * 2.687870 = 268.79 kg CO2e
        assertEquals(new BigDecimal("268.79"), response.getFuelItems().get(0).getEmissionsKg());

        // Petrol: 50 * 2.314400 = 115.72 kg CO2e
        assertEquals(new BigDecimal("115.72"), response.getFuelItems().get(1).getEmissionsKg());

        // Natural Gas: 500 * 0.182930 = 91.47 kg CO2e
        assertEquals(new BigDecimal("91.47"), response.getFuelItems().get(2).getEmissionsKg());

        BigDecimal expectedTotalFuel = new BigDecimal("475.97");
        assertEquals(expectedTotalFuel, response.getTotalFuelEmissionsKg());
        assertEquals(expectedTotalFuel, response.getScope1EmissionsKg());
    }

    @Test
    @DisplayName("Should calculate transport activity with Scope 3")
    void shouldCalculateTransportEmissions() {
        CarbonCalculationRequest request = new CarbonCalculationRequest();
        request.setTransportEntries(List.of(
                new TransportInputDto("PASSENGER_CAR_PETROL", new BigDecimal("500.00"), "KM"),
                new TransportInputDto("ELECTRIC_VEHICLE", new BigDecimal("200.00"), "KM"),
                new TransportInputDto("RAIL_TRAIN", new BigDecimal("300.00"), "KM")
        ));

        CarbonCalculationResponse response = provider.calculate(request);

        assertNotNull(response);
        assertEquals(3, response.getTransportItems().size());

        // Car Petrol: 500 * 0.170480 = 85.24 kg
        assertEquals(new BigDecimal("85.24"), response.getTransportItems().get(0).getEmissionsKg());

        // EV: 200 * 0.047100 = 9.42 kg
        assertEquals(new BigDecimal("9.42"), response.getTransportItems().get(1).getEmissionsKg());

        // Train: 300 * 0.035490 = 10.65 kg
        assertEquals(new BigDecimal("10.65"), response.getTransportItems().get(2).getEmissionsKg());

        BigDecimal expectedTotalTransport = new BigDecimal("105.31");
        assertEquals(expectedTotalTransport, response.getTotalTransportEmissionsKg());
        assertEquals(expectedTotalTransport, response.getScope3EmissionsKg());
    }

    @Test
    @DisplayName("Should calculate mixed activities with accurate scope percentage breakdown")
    void shouldCalculateMixedActivitiesAndScopeBreakdown() {
        CarbonCalculationRequest request = new CarbonCalculationRequest();
        request.setElectricityKwh(new BigDecimal("500.00")); // Scope 2: 500 * 0.42 = 210.00 kg
        request.setFuelEntries(List.of(
                new FuelInputDto("DIESEL", new BigDecimal("100.00"), "LITERS") // Scope 1: 100 * 2.68787 = 268.79 kg
        ));
        request.setTransportEntries(List.of(
                new TransportInputDto("PASSENGER_CAR_PETROL", new BigDecimal("1000.00"), "KM") // Scope 3: 1000 * 0.17048 = 170.48 kg
        ));

        CarbonCalculationResponse response = provider.calculate(request);

        assertEquals(new BigDecimal("268.79"), response.getScope1EmissionsKg());
        assertEquals(new BigDecimal("210.00"), response.getScope2EmissionsKg());
        assertEquals(new BigDecimal("170.48"), response.getScope3EmissionsKg());

        // Total: 268.79 + 210.00 + 170.48 = 649.27 kg CO2e
        assertEquals(new BigDecimal("649.27"), response.getTotalEmissionsKg());
        assertEquals(new BigDecimal("0.6493"), response.getTotalEmissionsTonnes());

        // Percentages should add up to approx 100%
        BigDecimal sumPct = response.getScope1Percentage()
                .add(response.getScope2Percentage())
                .add(response.getScope3Percentage());
        assertTrue(sumPct.compareTo(new BigDecimal("99.90")) >= 0 && sumPct.compareTo(new BigDecimal("100.10")) <= 0);
    }

    @Test
    @DisplayName("Should throw exception on negative values")
    void shouldThrowExceptionOnNegativeValues() {
        CarbonCalculationRequest req1 = new CarbonCalculationRequest();
        req1.setElectricityKwh(new BigDecimal("-10"));
        assertThrows(IllegalArgumentException.class, () -> provider.calculate(req1));

        CarbonCalculationRequest req2 = new CarbonCalculationRequest();
        req2.setFuelEntries(List.of(new FuelInputDto("DIESEL", new BigDecimal("-5"), "LITERS")));
        assertThrows(IllegalArgumentException.class, () -> provider.calculate(req2));

        CarbonCalculationRequest req3 = new CarbonCalculationRequest();
        req3.setTransportEntries(List.of(new TransportInputDto("PASSENGER_CAR_PETROL", new BigDecimal("-100"), "KM")));
        assertThrows(IllegalArgumentException.class, () -> provider.calculate(req3));
    }

    @Test
    @DisplayName("Should throw exception on unsupported fuel or transport type")
    void shouldThrowExceptionOnUnsupportedType() {
        CarbonCalculationRequest reqFuel = new CarbonCalculationRequest();
        reqFuel.setFuelEntries(List.of(new FuelInputDto("UNKNOWN_COAL", new BigDecimal("10"), "TONNES")));
        assertThrows(IllegalArgumentException.class, () -> provider.calculate(reqFuel));

        CarbonCalculationRequest reqTransport = new CarbonCalculationRequest();
        reqTransport.setTransportEntries(List.of(new TransportInputDto("ROCKET_SHIP", new BigDecimal("100"), "KM")));
        assertThrows(IllegalArgumentException.class, () -> provider.calculate(reqTransport));
    }
}
