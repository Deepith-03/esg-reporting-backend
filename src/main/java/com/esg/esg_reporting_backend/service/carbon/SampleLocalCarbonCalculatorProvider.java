package com.esg.esg_reporting_backend.service.carbon;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.esg.esg_reporting_backend.dto.carbon.CarbonCalculationRequest;
import com.esg.esg_reporting_backend.dto.carbon.CarbonCalculationResponse;
import com.esg.esg_reporting_backend.dto.carbon.ElectricityCalculationResultDto;
import com.esg.esg_reporting_backend.dto.carbon.EmissionFactorDto;
import com.esg.esg_reporting_backend.dto.carbon.FuelCalculationResultDto;
import com.esg.esg_reporting_backend.dto.carbon.FuelInputDto;
import com.esg.esg_reporting_backend.dto.carbon.TransportCalculationResultDto;
import com.esg.esg_reporting_backend.dto.carbon.TransportInputDto;

@Component
public class SampleLocalCarbonCalculatorProvider implements CarbonCalculationProvider {

    public static final String PROVIDER_ID = "SAMPLE_LOCAL_CALCULATION";
    public static final String PROVIDER_NAME = "Sample Local Carbon Calculator (Development / Demo Mode)";
    public static final String SAMPLE_DISCLAIMER =
            "NOTICE: These emissions are calculated using sample reference emission factors for development and demonstration purposes. "
            + "They do not represent certified official values. When production regulatory factors or an external certified Carbon Calculator API "
            + "are configured, this service can substitute them seamlessly.";

    private static final BigDecimal KG_PER_TONNE = new BigDecimal("1000");
    private static final int CALCULATION_SCALE = 6;
    private static final int DISPLAY_SCALE = 2;

    private final BigDecimal electricityFactorKwh;

    // Fuel factor definition record
    public static class FuelFactorSpec {
        public final String fuelType;
        public final String displayName;
        public final String unit;
        public final BigDecimal factorKgCo2e;
        public final String scope;
        public final String sourceCitation;
        public final String notes;

        public FuelFactorSpec(String fuelType, String displayName, String unit, BigDecimal factorKgCo2e,
                              String scope, String sourceCitation, String notes) {
            this.fuelType = fuelType;
            this.displayName = displayName;
            this.unit = unit;
            this.factorKgCo2e = factorKgCo2e;
            this.scope = scope;
            this.sourceCitation = sourceCitation;
            this.notes = notes;
        }
    }

    // Transport factor definition record
    public static class TransportFactorSpec {
        public final String transportType;
        public final String displayName;
        public final String unit;
        public final BigDecimal factorKgCo2e;
        public final String scope;
        public final String sourceCitation;
        public final String notes;

        public TransportFactorSpec(String transportType, String displayName, String unit, BigDecimal factorKgCo2e,
                                  String scope, String sourceCitation, String notes) {
            this.transportType = transportType;
            this.displayName = displayName;
            this.unit = unit;
            this.factorKgCo2e = factorKgCo2e;
            this.scope = scope;
            this.sourceCitation = sourceCitation;
            this.notes = notes;
        }
    }

    private final Map<String, FuelFactorSpec> fuelFactors = new LinkedHashMap<>();
    private final Map<String, TransportFactorSpec> transportFactors = new LinkedHashMap<>();

    public SampleLocalCarbonCalculatorProvider(
            @Value("${carbon.calculator.factors.electricity-kwh:0.420000}") String electricityFactorStr) {
        this.electricityFactorKwh = new BigDecimal(electricityFactorStr);
        initializeFactors();
    }

    private void initializeFactors() {
        // --- FUELS (Scope 1) ---
        registerFuel(new FuelFactorSpec(
                "DIESEL", "Diesel Fuel", "LITERS", new BigDecimal("2.687870"), "SCOPE_1",
                "UK DESNZ / GHG Protocol 2023 - Mineral Diesel",
                "Sample factor: 2.68787 kg CO2e per liter"));
        registerFuel(new FuelFactorSpec(
                "DIESEL", "Diesel Fuel", "GALLONS", new BigDecimal("10.174700"), "SCOPE_1",
                "UK DESNZ / US EPA 2023 - Diesel (US Gallons)",
                "Sample factor: 10.1747 kg CO2e per US gallon"));

        registerFuel(new FuelFactorSpec(
                "PETROL", "Petrol / Gasoline", "LITERS", new BigDecimal("2.314400"), "SCOPE_1",
                "UK DESNZ / GHG Protocol 2023 - Petrol (average biofuel blend)",
                "Sample factor: 2.3144 kg CO2e per liter"));
        registerFuel(new FuelFactorSpec(
                "PETROL", "Petrol / Gasoline", "GALLONS", new BigDecimal("8.760900"), "SCOPE_1",
                "UK DESNZ / US EPA 2023 - Motor Gasoline (US Gallons)",
                "Sample factor: 8.7609 kg CO2e per US gallon"));

        registerFuel(new FuelFactorSpec(
                "NATURAL_GAS", "Natural Gas", "KWH", new BigDecimal("0.182930"), "SCOPE_1",
                "UK DESNZ / GHG Protocol 2023 - Natural Gas (Gross CV)",
                "Sample factor: 0.18293 kg CO2e per kWh"));
        registerFuel(new FuelFactorSpec(
                "NATURAL_GAS", "Natural Gas", "M3", new BigDecimal("2.028000"), "SCOPE_1",
                "UK DESNZ 2023 - Natural Gas per cubic meter",
                "Sample factor: 2.028 kg CO2e per m3"));

        registerFuel(new FuelFactorSpec(
                "LPG", "LPG (Liquefied Petroleum Gas)", "LITERS", new BigDecimal("1.557000"), "SCOPE_1",
                "UK DESNZ 2023 - LPG per liter",
                "Sample factor: 1.557 kg CO2e per liter"));
        registerFuel(new FuelFactorSpec(
                "LPG", "LPG (Liquefied Petroleum Gas)", "KG", new BigDecimal("2.939000"), "SCOPE_1",
                "UK DESNZ 2023 - LPG per kilogram",
                "Sample factor: 2.939 kg CO2e per kg"));

        registerFuel(new FuelFactorSpec(
                "HEAVY_FUEL_OIL", "Heavy Fuel Oil", "LITERS", new BigDecimal("2.962000"), "SCOPE_1",
                "UK DESNZ 2023 - Fuel oil per liter",
                "Sample factor: 2.962 kg CO2e per liter"));

        // --- TRANSPORTATION (Scope 3 / Scope 1) ---
        registerTransport(new TransportFactorSpec(
                "PASSENGER_CAR_PETROL", "Passenger Car (Petrol)", "KM", new BigDecimal("0.170480"), "SCOPE_3",
                "UK DESNZ 2023 Passenger Vehicles - Average petrol car",
                "Sample factor: 0.17048 kg CO2e per km"));
        registerTransport(new TransportFactorSpec(
                "PASSENGER_CAR_PETROL", "Passenger Car (Petrol)", "MILES", new BigDecimal("0.274360"), "SCOPE_3",
                "UK DESNZ 2023 Passenger Vehicles - Average petrol car",
                "Sample factor: 0.27436 kg CO2e per mile"));

        registerTransport(new TransportFactorSpec(
                "PASSENGER_CAR_DIESEL", "Passenger Car (Diesel)", "KM", new BigDecimal("0.165600"), "SCOPE_3",
                "UK DESNZ 2023 Passenger Vehicles - Average diesel car",
                "Sample factor: 0.16560 kg CO2e per km"));
        registerTransport(new TransportFactorSpec(
                "PASSENGER_CAR_DIESEL", "Passenger Car (Diesel)", "MILES", new BigDecimal("0.266500"), "SCOPE_3",
                "UK DESNZ 2023 Passenger Vehicles - Average diesel car",
                "Sample factor: 0.26650 kg CO2e per mile"));

        registerTransport(new TransportFactorSpec(
                "ELECTRIC_VEHICLE", "Electric Vehicle (EV)", "KM", new BigDecimal("0.047100"), "SCOPE_3",
                "UK DESNZ 2023 Passenger Vehicles - Battery electric car (grid mix)",
                "Sample factor: 0.04710 kg CO2e per km"));
        registerTransport(new TransportFactorSpec(
                "ELECTRIC_VEHICLE", "Electric Vehicle (EV)", "MILES", new BigDecimal("0.075800"), "SCOPE_3",
                "UK DESNZ 2023 Passenger Vehicles - Battery electric car",
                "Sample factor: 0.07580 kg CO2e per mile"));

        registerTransport(new TransportFactorSpec(
                "VAN_LIGHT_COMMERCIAL", "Light Commercial Van", "KM", new BigDecimal("0.241200"), "SCOPE_3",
                "UK DESNZ 2023 Delivery Vehicles - Class II van up to 3.5t",
                "Sample factor: 0.24120 kg CO2e per km"));
        registerTransport(new TransportFactorSpec(
                "VAN_LIGHT_COMMERCIAL", "Light Commercial Van", "MILES", new BigDecimal("0.388170"), "SCOPE_3",
                "UK DESNZ 2023 Delivery Vehicles - Class II van up to 3.5t",
                "Sample factor: 0.38817 kg CO2e per mile"));

        registerTransport(new TransportFactorSpec(
                "HEAVY_TRUCK_FREIGHT", "Heavy Freight Truck (HGV)", "KM", new BigDecimal("0.852000"), "SCOPE_3",
                "UK DESNZ 2023 Freight Transport - Rigid HGV >17t average laden",
                "Sample factor: 0.85200 kg CO2e per km"));
        registerTransport(new TransportFactorSpec(
                "HEAVY_TRUCK_FREIGHT", "Heavy Freight Truck (HGV)", "MILES", new BigDecimal("1.371180"), "SCOPE_3",
                "UK DESNZ 2023 Freight Transport - Rigid HGV >17t average laden",
                "Sample factor: 1.37118 kg CO2e per mile"));

        registerTransport(new TransportFactorSpec(
                "RAIL_TRAIN", "Rail / Passenger Train", "KM", new BigDecimal("0.035490"), "SCOPE_3",
                "UK DESNZ 2023 Business Travel - National rail per passenger-km",
                "Sample factor: 0.03549 kg CO2e per passenger-km"));
        registerTransport(new TransportFactorSpec(
                "RAIL_TRAIN", "Rail / Passenger Train", "MILES", new BigDecimal("0.057110"), "SCOPE_3",
                "UK DESNZ 2023 Business Travel - National rail per passenger-mile",
                "Sample factor: 0.05711 kg CO2e per passenger-mile"));

        registerTransport(new TransportFactorSpec(
                "AIR_DOMESTIC", "Flight (Domestic)", "KM", new BigDecimal("0.245870"), "SCOPE_3",
                "UK DESNZ 2023 Business Travel - Domestic aviation with radiative forcing",
                "Sample factor: 0.24587 kg CO2e per passenger-km"));
        registerTransport(new TransportFactorSpec(
                "AIR_DOMESTIC", "Flight (Domestic)", "MILES", new BigDecimal("0.395680"), "SCOPE_3",
                "UK DESNZ 2023 Business Travel - Domestic aviation with radiative forcing",
                "Sample factor: 0.39568 kg CO2e per passenger-mile"));

        registerTransport(new TransportFactorSpec(
                "AIR_INTERNATIONAL", "Flight (International / Long-haul)", "KM", new BigDecimal("0.183620"), "SCOPE_3",
                "UK DESNZ 2023 Business Travel - Long-haul flight average class with RF",
                "Sample factor: 0.18362 kg CO2e per passenger-km"));
        registerTransport(new TransportFactorSpec(
                "AIR_INTERNATIONAL", "Flight (International / Long-haul)", "MILES", new BigDecimal("0.295500"), "SCOPE_3",
                "UK DESNZ 2023 Business Travel - Long-haul flight average class with RF",
                "Sample factor: 0.29550 kg CO2e per passenger-mile"));
    }

    private void registerFuel(FuelFactorSpec spec) {
        String key = (spec.fuelType + "_" + spec.unit).toUpperCase();
        fuelFactors.put(key, spec);
    }

    private void registerTransport(TransportFactorSpec spec) {
        String key = (spec.transportType + "_" + spec.unit).toUpperCase();
        transportFactors.put(key, spec);
    }

    @Override
    public String getProviderIdentifier() {
        return PROVIDER_ID;
    }

    @Override
    public String getProviderDisplayName() {
        return PROVIDER_NAME;
    }

    @Override
    public boolean isOfficial() {
        return false;
    }

    @Override
    public List<EmissionFactorDto> getAvailableFactors() {
        List<EmissionFactorDto> list = new ArrayList<>();

        // Electricity
        list.add(new EmissionFactorDto(
                "ELECTRICITY",
                "GRID_ELECTRICITY",
                "Grid Electricity",
                "KWH",
                electricityFactorKwh,
                "kg CO2e / kWh",
                "SCOPE_2",
                "Sample dev factor based on US EPA eGRID / IEA global indicative reference grid average",
                true,
                "Indicative average grid emission factor for demonstration (~0.42 kg CO2e/kWh)"));

        // Fuels
        for (FuelFactorSpec spec : fuelFactors.values()) {
            list.add(new EmissionFactorDto(
                    "FUEL",
                    spec.fuelType,
                    spec.displayName,
                    spec.unit,
                    spec.factorKgCo2e,
                    "kg CO2e / " + spec.unit.toLowerCase(),
                    spec.scope,
                    spec.sourceCitation,
                    true,
                    spec.notes));
        }

        // Transports
        for (TransportFactorSpec spec : transportFactors.values()) {
            list.add(new EmissionFactorDto(
                    "TRANSPORT",
                    spec.transportType,
                    spec.displayName,
                    spec.unit,
                    spec.factorKgCo2e,
                    "kg CO2e / " + spec.unit.toLowerCase(),
                    spec.scope,
                    spec.sourceCitation,
                    true,
                    spec.notes));
        }

        return Collections.unmodifiableList(list);
    }

    @Override
    public CarbonCalculationResponse calculate(CarbonCalculationRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Calculation request cannot be null");
        }

        BigDecimal electricityKwh = request.getElectricityKwh() != null ? request.getElectricityKwh() : BigDecimal.ZERO;
        if (electricityKwh.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Electricity consumption cannot be negative");
        }

        CarbonCalculationResponse response = new CarbonCalculationResponse();
        response.setCalculationMethod(PROVIDER_ID);
        response.setProviderName(PROVIDER_NAME);
        response.setOfficial(false);
        response.setDisclaimer(SAMPLE_DISCLAIMER);
        response.setCalculatedAt(LocalDateTime.now());

        // 1. Calculate Electricity (Scope 2)
        BigDecimal electricityEmissionsKg = electricityKwh
                .multiply(electricityFactorKwh)
                .setScale(CALCULATION_SCALE, RoundingMode.HALF_UP);
        BigDecimal electricityEmissionsTonnes = electricityEmissionsKg
                .divide(KG_PER_TONNE, CALCULATION_SCALE, RoundingMode.HALF_UP);

        String electricityFormula = String.format("%.2f kWh * %.6f kg CO2e/kWh = %.2f kg CO2e (%.4f tonnes CO2e)",
                electricityKwh.setScale(2, RoundingMode.HALF_UP),
                electricityFactorKwh,
                electricityEmissionsKg.setScale(DISPLAY_SCALE, RoundingMode.HALF_UP),
                electricityEmissionsTonnes.setScale(4, RoundingMode.HALF_UP));

        ElectricityCalculationResultDto electricityResult = new ElectricityCalculationResultDto(
                electricityKwh.setScale(2, RoundingMode.HALF_UP),
                electricityFactorKwh,
                "kg CO2e / kWh",
                "US EPA eGRID / IEA global indicative reference average (~0.42 kg CO2e/kWh)",
                electricityEmissionsKg.setScale(DISPLAY_SCALE, RoundingMode.HALF_UP),
                electricityEmissionsTonnes.setScale(4, RoundingMode.HALF_UP),
                "Scope 2 (Indirect - Purchased Electricity)",
                electricityFormula);
        response.setElectricity(electricityResult);

        // 2. Calculate Fuels (Scope 1)
        BigDecimal totalFuelKg = BigDecimal.ZERO;
        List<FuelCalculationResultDto> fuelResults = new ArrayList<>();

        if (request.getFuelEntries() != null) {
            for (FuelInputDto fuelEntry : request.getFuelEntries()) {
                if (fuelEntry == null) continue;
                String fuelType = fuelEntry.getFuelType() != null ? fuelEntry.getFuelType().trim().toUpperCase() : "";
                String unit = fuelEntry.getUnit() != null ? fuelEntry.getUnit().trim().toUpperCase() : "";
                BigDecimal qty = fuelEntry.getQuantity() != null ? fuelEntry.getQuantity() : BigDecimal.ZERO;

                if (qty.compareTo(BigDecimal.ZERO) < 0) {
                    throw new IllegalArgumentException("Fuel quantity cannot be negative for fuel type: " + fuelType);
                }

                String lookupKey = fuelType + "_" + unit;
                FuelFactorSpec factorSpec = fuelFactors.get(lookupKey);
                if (factorSpec == null) {
                    // Try case-insensitive matching
                    factorSpec = fuelFactors.entrySet().stream()
                            .filter(e -> e.getKey().equalsIgnoreCase(lookupKey))
                            .map(Map.Entry::getValue)
                            .findFirst()
                            .orElse(null);
                }

                if (factorSpec == null) {
                    throw new IllegalArgumentException("Unsupported fuel type and unit combination: " + fuelType + " (" + unit + ")");
                }

                BigDecimal itemKg = qty.multiply(factorSpec.factorKgCo2e).setScale(CALCULATION_SCALE, RoundingMode.HALF_UP);
                BigDecimal itemTonnes = itemKg.divide(KG_PER_TONNE, CALCULATION_SCALE, RoundingMode.HALF_UP);

                String formula = String.format("%.2f %s * %.6f kg CO2e/%s = %.2f kg CO2e (%.4f tonnes CO2e)",
                        qty.setScale(2, RoundingMode.HALF_UP),
                        unit.toLowerCase(),
                        factorSpec.factorKgCo2e,
                        unit.toLowerCase(),
                        itemKg.setScale(DISPLAY_SCALE, RoundingMode.HALF_UP),
                        itemTonnes.setScale(4, RoundingMode.HALF_UP));

                FuelCalculationResultDto itemResult = new FuelCalculationResultDto(
                        factorSpec.fuelType,
                        factorSpec.displayName,
                        qty.setScale(2, RoundingMode.HALF_UP),
                        factorSpec.unit,
                        factorSpec.factorKgCo2e,
                        "kg CO2e / " + factorSpec.unit.toLowerCase(),
                        factorSpec.sourceCitation,
                        itemKg.setScale(DISPLAY_SCALE, RoundingMode.HALF_UP),
                        itemTonnes.setScale(4, RoundingMode.HALF_UP),
                        "Scope 1 (Direct - Fuel Combustion)",
                        formula);
                fuelResults.add(itemResult);
                totalFuelKg = totalFuelKg.add(itemKg);
            }
        }
        response.setFuelItems(fuelResults);

        // 3. Calculate Transportation (Scope 3)
        BigDecimal totalTransportKg = BigDecimal.ZERO;
        List<TransportCalculationResultDto> transportResults = new ArrayList<>();

        if (request.getTransportEntries() != null) {
            for (TransportInputDto transportEntry : request.getTransportEntries()) {
                if (transportEntry == null) continue;
                String transportType = transportEntry.getTransportType() != null ? transportEntry.getTransportType().trim().toUpperCase() : "";
                String unit = transportEntry.getUnit() != null ? transportEntry.getUnit().trim().toUpperCase() : "";
                BigDecimal dist = transportEntry.getDistance() != null ? transportEntry.getDistance() : BigDecimal.ZERO;

                if (dist.compareTo(BigDecimal.ZERO) < 0) {
                    throw new IllegalArgumentException("Transport distance cannot be negative for transport type: " + transportType);
                }

                String lookupKey = transportType + "_" + unit;
                TransportFactorSpec factorSpec = transportFactors.get(lookupKey);
                if (factorSpec == null) {
                    factorSpec = transportFactors.entrySet().stream()
                            .filter(e -> e.getKey().equalsIgnoreCase(lookupKey))
                            .map(Map.Entry::getValue)
                            .findFirst()
                            .orElse(null);
                }

                if (factorSpec == null) {
                    throw new IllegalArgumentException("Unsupported transport type and unit combination: " + transportType + " (" + unit + ")");
                }

                BigDecimal itemKg = dist.multiply(factorSpec.factorKgCo2e).setScale(CALCULATION_SCALE, RoundingMode.HALF_UP);
                BigDecimal itemTonnes = itemKg.divide(KG_PER_TONNE, CALCULATION_SCALE, RoundingMode.HALF_UP);

                String formula = String.format("%.2f %s * %.6f kg CO2e/%s = %.2f kg CO2e (%.4f tonnes CO2e)",
                        dist.setScale(2, RoundingMode.HALF_UP),
                        unit.toLowerCase(),
                        factorSpec.factorKgCo2e,
                        unit.toLowerCase(),
                        itemKg.setScale(DISPLAY_SCALE, RoundingMode.HALF_UP),
                        itemTonnes.setScale(4, RoundingMode.HALF_UP));

                TransportCalculationResultDto itemResult = new TransportCalculationResultDto(
                        factorSpec.transportType,
                        factorSpec.displayName,
                        dist.setScale(2, RoundingMode.HALF_UP),
                        factorSpec.unit,
                        factorSpec.factorKgCo2e,
                        "kg CO2e / " + factorSpec.unit.toLowerCase(),
                        factorSpec.sourceCitation,
                        itemKg.setScale(DISPLAY_SCALE, RoundingMode.HALF_UP),
                        itemTonnes.setScale(4, RoundingMode.HALF_UP),
                        "Scope 3 (Indirect - Business Travel & Logistics)",
                        formula);
                transportResults.add(itemResult);
                totalTransportKg = totalTransportKg.add(itemKg);
            }
        }
        response.setTransportItems(transportResults);

        // 4. Summarize category totals
        BigDecimal totalFuelTonnes = totalFuelKg.divide(KG_PER_TONNE, CALCULATION_SCALE, RoundingMode.HALF_UP);
        BigDecimal totalTransportTonnes = totalTransportKg.divide(KG_PER_TONNE, CALCULATION_SCALE, RoundingMode.HALF_UP);

        response.setTotalElectricityEmissionsKg(electricityEmissionsKg.setScale(DISPLAY_SCALE, RoundingMode.HALF_UP));
        response.setTotalElectricityEmissionsTonnes(electricityEmissionsTonnes.setScale(4, RoundingMode.HALF_UP));

        response.setTotalFuelEmissionsKg(totalFuelKg.setScale(DISPLAY_SCALE, RoundingMode.HALF_UP));
        response.setTotalFuelEmissionsTonnes(totalFuelTonnes.setScale(4, RoundingMode.HALF_UP));

        response.setTotalTransportEmissionsKg(totalTransportKg.setScale(DISPLAY_SCALE, RoundingMode.HALF_UP));
        response.setTotalTransportEmissionsTonnes(totalTransportTonnes.setScale(4, RoundingMode.HALF_UP));

        // 5. Scopes:
        // Scope 1 = Direct Fuel Combustion
        // Scope 2 = Purchased Electricity
        // Scope 3 = Transportation / Logistics
        BigDecimal scope1Kg = totalFuelKg;
        BigDecimal scope2Kg = electricityEmissionsKg;
        BigDecimal scope3Kg = totalTransportKg;

        response.setScope1EmissionsKg(scope1Kg.setScale(DISPLAY_SCALE, RoundingMode.HALF_UP));
        response.setScope1EmissionsTonnes(scope1Kg.divide(KG_PER_TONNE, 4, RoundingMode.HALF_UP));

        response.setScope2EmissionsKg(scope2Kg.setScale(DISPLAY_SCALE, RoundingMode.HALF_UP));
        response.setScope2EmissionsTonnes(scope2Kg.divide(KG_PER_TONNE, 4, RoundingMode.HALF_UP));

        response.setScope3EmissionsKg(scope3Kg.setScale(DISPLAY_SCALE, RoundingMode.HALF_UP));
        response.setScope3EmissionsTonnes(scope3Kg.divide(KG_PER_TONNE, 4, RoundingMode.HALF_UP));

        // 6. Overall Grand Total
        BigDecimal totalKg = scope1Kg.add(scope2Kg).add(scope3Kg);
        BigDecimal totalTonnes = totalKg.divide(KG_PER_TONNE, 4, RoundingMode.HALF_UP);

        response.setTotalEmissionsKg(totalKg.setScale(DISPLAY_SCALE, RoundingMode.HALF_UP));
        response.setTotalEmissionsTonnes(totalTonnes);

        // 7. Scope Percentages
        if (totalKg.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal hundred = new BigDecimal("100");
            response.setScope1Percentage(scope1Kg.multiply(hundred).divide(totalKg, 2, RoundingMode.HALF_UP));
            response.setScope2Percentage(scope2Kg.multiply(hundred).divide(totalKg, 2, RoundingMode.HALF_UP));
            response.setScope3Percentage(scope3Kg.multiply(hundred).divide(totalKg, 2, RoundingMode.HALF_UP));
        } else {
            response.setScope1Percentage(BigDecimal.ZERO.setScale(2));
            response.setScope2Percentage(BigDecimal.ZERO.setScale(2));
            response.setScope3Percentage(BigDecimal.ZERO.setScale(2));
        }

        return response;
    }
}
