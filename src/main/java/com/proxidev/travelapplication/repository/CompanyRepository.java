package com.proxidev.travelapplication.repository;

import com.proxidev.travelapplication.entity.Company;
import com.proxidev.travelapplication.enums.CompanyStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CompanyRepository extends JpaRepository<Company, UUID> {
    Optional<Company> findBySlug(String slug);

    Boolean existsBySlug(String slug);

    List<Company> findByStatusOrderByCreatedAtDesc(CompanyStatus status);

    List<Company> findByStatus(CompanyStatus status);

}
