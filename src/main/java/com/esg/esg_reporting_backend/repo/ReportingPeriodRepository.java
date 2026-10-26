package com.esg.esg_reporting_backend.repo;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.esg.esg_reporting_backend.entity.ReportingPeriod;

public interface ReportingPeriodRepository extends JpaRepository<ReportingPeriod, Long> {

    List<ReportingPeriod> findByOrganizationId(Long organizationId);
}