package com.proxidev.travelapplication.repository;

import com.proxidev.travelapplication.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRoleRepository extends JpaRepository<UserRole, UUID> {
    Optional<UserRole> findByUserIdAndRoleId(UUID userId, int roleId);
    boolean existsByUserIdAndRoleId(UUID userId, int roleId);

    @Query("""
            SELECT ur FROM UserRole ur
            JOIN FETCH ur.role r
            JOIN FETCH r.permissions
            WHERE ur.userId = :userId
            """)
    List<UserRole> findByUserIdFetchRolePermissions(UUID userId);
}