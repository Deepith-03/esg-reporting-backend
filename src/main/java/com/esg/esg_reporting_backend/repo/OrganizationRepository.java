package com.esg.esg_reporting_backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.esg.esg_reporting_backend.entity.Organization;

public interface OrganizationRepository
        extends JpaRepository<Organization, Long> {
}