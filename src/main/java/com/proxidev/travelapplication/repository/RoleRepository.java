package com.proxidev.travelapplication.repository;

import com.proxidev.travelapplication.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    List<Role> findByCompanyIdOrderByCreatedAtAsc(UUID companyId);

    Optional<Role> findByIdAndCompanyId(int id, UUID companyId);

    Optional<Role> findByNameAndCompanyId(String name, UUID companyId);

    Optional<Role> findByNameAndSystemTrue(String name);
}