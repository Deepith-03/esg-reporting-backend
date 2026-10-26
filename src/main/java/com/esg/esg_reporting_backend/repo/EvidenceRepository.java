
package com.esg.esg_reporting_backend.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.esg.esg_reporting_backend.entity.Evidence;

public interface EvidenceRepository extends JpaRepository<Evidence, Long> {

    List<Evidence> findByMetricEntryId(Long metricEntryId);
}
