
package com.esg.esg_reporting_backend.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class EsgCalculationService {

    private static final BigDecimal ONE_HUNDRED =
            new BigDecimal("100");

    private static final int SCALE = 2;

    /**
     * Recycling Rate = (Recycled Waste / Total Waste) * 100
     */
    public Optional<BigDecimal> recyclingRate(
            BigDecimal totalWaste,
            BigDecimal recycledWaste) {

        validateNonNegative(totalWaste, "Total waste");
        validateNonNegative(recycledWaste, "Recycled waste");

        if (recycledWaste.compareTo(totalWaste) > 0) {
            throw new IllegalArgumentException(
                    "Recycled waste cannot exceed total waste");
        }

        if (totalWaste.compareTo(BigDecimal.ZERO) == 0) {
            return Optional.empty();
        }

        BigDecimal rate = recycledWaste
                .divide(totalWaste, 4, RoundingMode.HALF_UP)
                .multiply(ONE_HUNDRED);

        return Optional.of(rate.setScale(SCALE, RoundingMode.HALF_UP));
    }

    /**
     * Turnover Rate = (Departures / Average Headcount) * 100
     *
     * Average Headcount = (Start Headcount + End Headcount) / 2
     *
     * Turnover can legitimately exceed 100%, so departures are not
     * restricted to being less than or equal to average headcount.
     */
    public Optional<BigDecimal> turnoverRate(
            BigDecimal startHeadcount,
            BigDecimal endHeadcount,
            BigDecimal departures) {

        validateNonNegative(startHeadcount, "Start headcount");
        validateNonNegative(endHeadcount, "End headcount");
        validateNonNegative(departures, "Departures");

        BigDecimal averageHeadcount = startHeadcount
                .add(endHeadcount)
                .divide(new BigDecimal("2"), 4, RoundingMode.HALF_UP);

        if (averageHeadcount.compareTo(BigDecimal.ZERO) == 0) {
            return Optional.empty();
        }

        BigDecimal rate = departures
                .divide(averageHeadcount, 4, RoundingMode.HALF_UP)
                .multiply(ONE_HUNDRED);

        return Optional.of(rate.setScale(SCALE, RoundingMode.HALF_UP));
    }

    /**
     * Training Hours per Employee = Total Training Hours / Average Headcount
     */
    public Optional<BigDecimal> trainingHoursPerEmployee(
            BigDecimal totalHours,
            BigDecimal startHeadcount,
            BigDecimal endHeadcount) {

        validateNonNegative(totalHours, "Total training hours");
        validateNonNegative(startHeadcount, "Start headcount");
        validateNonNegative(endHeadcount, "End headcount");

        BigDecimal averageHeadcount = startHeadcount
                .add(endHeadcount)
                .divide(new BigDecimal("2"), 4, RoundingMode.HALF_UP);

        if (averageHeadcount.compareTo(BigDecimal.ZERO) == 0) {
            return Optional.empty();
        }

        BigDecimal result = totalHours.divide(
                averageHeadcount, SCALE, RoundingMode.HALF_UP);

        return Optional.of(result);
    }

    /**
     * Board Independence = (Independent Members / Total Members) * 100
     */
    public Optional<BigDecimal> boardIndependence(
            BigDecimal totalMembers,
            BigDecimal independentMembers) {

        validateNonNegative(totalMembers, "Total board members");
        validateNonNegative(independentMembers, "Independent board members");

        if (independentMembers.compareTo(totalMembers) > 0) {
            throw new IllegalArgumentException(
                    "Independent members cannot exceed total members");
        }

        if (totalMembers.compareTo(BigDecimal.ZERO) == 0) {
            return Optional.empty();
        }

        BigDecimal rate = independentMembers
                .divide(totalMembers, 4, RoundingMode.HALF_UP)
                .multiply(ONE_HUNDRED);

        return Optional.of(rate.setScale(SCALE, RoundingMode.HALF_UP));
    }

    /**
     * Training Completion = (Completed People / Eligible People) * 100
     */
    public Optional<BigDecimal> trainingCompletion(
            BigDecimal eligiblePeople,
            BigDecimal completedPeople) {

        validateNonNegative(eligiblePeople, "Eligible people");
        validateNonNegative(completedPeople, "Completed people");

        if (completedPeople.compareTo(eligiblePeople) > 0) {
            throw new IllegalArgumentException(
                    "Completed people cannot exceed eligible people");
        }

        if (eligiblePeople.compareTo(BigDecimal.ZERO) == 0) {
            return Optional.empty();
        }

        BigDecimal rate = completedPeople
                .divide(eligiblePeople, 4, RoundingMode.HALF_UP)
                .multiply(ONE_HUNDRED);

        return Optional.of(rate.setScale(SCALE, RoundingMode.HALF_UP));
    }

    private void validateNonNegative(
            BigDecimal value,
            String fieldName) {

        if (value == null) {
            throw new IllegalArgumentException(
                    fieldName + " cannot be null");
        }

        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    fieldName + " cannot be negative");
        }
    }
}
