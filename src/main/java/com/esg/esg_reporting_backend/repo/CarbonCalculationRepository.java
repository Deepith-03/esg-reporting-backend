package com.esg.esg_reporting_backend.repo;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.esg.esg_reporting_backend.entity.CarbonCalculationRecord;

@Repository
public interface CarbonCalculationRepository extends JpaRepository<CarbonCalculationRecord, Long> {

    List<CarbonCalculationRecord> findAllByOrderByCalculatedAtDesc();

    List<CarbonCalculationRecord> findByOrganizationIdOrderByCalculatedAtDesc(Long organizationId);

    List<CarbonCalculationRecord> findByReportingPeriodIdOrderByCalculatedAtDesc(Long reportingPeriodId);

    List<CarbonCalculationRecord> findByOrganizationIdAndReportingPeriodIdOrderByCalculatedAtDesc(Long organizationId, Long reportingPeriodId);

    void deleteByOrganizationId(Long organizationId);

    void deleteByReportingPeriodId(Long reportingPeriodId);
}
