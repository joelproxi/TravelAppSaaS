package com.proxidev.travelapplication.repository;

import com.proxidev.travelapplication.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByName(String name);

    List<Permission> findAllByOrderByModuleAscNameAsc();

    List<Permission> findByIdIn(List<Integer> ids);
}
