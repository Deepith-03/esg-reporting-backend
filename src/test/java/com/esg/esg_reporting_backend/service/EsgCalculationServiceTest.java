
package com.esg.esg_reporting_backend.service;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EsgCalculationServiceTest {

    private EsgCalculationService service;

    @BeforeEach
    void setUp() {
        service = new EsgCalculationService();
    }

    @Test
    void shouldCalculateRecyclingRate() {
        var result = service.recyclingRate(
                new BigDecimal("100"),
                new BigDecimal("80"));

        assertEquals(
                new BigDecimal("80.00"),
                result.orElseThrow());
    }

    @Test
    void shouldRejectRecycledWasteGreaterThanTotal() {
        assertThrows(IllegalArgumentException.class,
                () -> service.recyclingRate(
                        new BigDecimal("100"),
                        new BigDecimal("120")));
    }

    @Test
    void shouldReturnEmptyForZeroWaste() {
        var result = service.recyclingRate(
                BigDecimal.ZERO, BigDecimal.ZERO);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldCalculateTurnoverRate() {
        var result = service.turnoverRate(
                new BigDecimal("100"),
                new BigDecimal("100"),
                new BigDecimal("10"));

        assertEquals(
                new BigDecimal("10.00"),
                result.orElseThrow());
    }

    @Test
    void turnoverRateCanExceed100Percent() {
        var result = service.turnoverRate(
                new BigDecimal("10"),
                new BigDecimal("10"),
                new BigDecimal("12"));

        assertEquals(
                new BigDecimal("120.00"),
                result.orElseThrow());
    }

    @Test
    void shouldCalculateTrainingIntensity() {
        var result = service.trainingHoursPerEmployee(
                new BigDecimal("200"),
                new BigDecimal("100"),
                new BigDecimal("100"));

        assertEquals(
                new BigDecimal("2.00"),
                result.orElseThrow());
    }

    @Test
    void shouldCalculateBoardIndependence() {
        var result = service.boardIndependence(
                new BigDecimal("5"),
                new BigDecimal("3"));

        assertEquals(
                new BigDecimal("60.00"),
                result.orElseThrow());
    }

    @Test
    void shouldRejectIndependentMembersExceedingBoardSize() {
        assertThrows(IllegalArgumentException.class,
                () -> service.boardIndependence(
                        new BigDecimal("5"),
                        new BigDecimal("6")));
    }

    @Test
    void shouldCalculateTrainingCompletion() {
        var result = service.trainingCompletion(
                new BigDecimal("100"),
                new BigDecimal("90"));

        assertEquals(
                new BigDecimal("90.00"),
                result.orElseThrow());
    }

    @Test
    void shouldRejectCompletionExceedingEligiblePeople() {
        assertThrows(IllegalArgumentException.class,
                () -> service.trainingCompletion(
                        new BigDecimal("100"),
                        new BigDecimal("101")));
    }

    @Test
    void shouldHandleZeroEligiblePeople() {
        var result = service.trainingCompletion(
                BigDecimal.ZERO,
                BigDecimal.ZERO);

        assertTrue(result.isEmpty());
    }
}
