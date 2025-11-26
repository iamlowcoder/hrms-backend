package com.yourcompany.hrms.repository;


import com.yourcompany.hrms.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {
}
