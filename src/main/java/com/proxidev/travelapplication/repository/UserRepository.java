package com.proxidev.travelapplication.repository;

import com.proxidev.travelapplication.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

        @Query("""
                        SELECT DISTINCT u FROM User u
                        LEFT JOIN FETCH u.userRoles ur
                        LEFT JOIN FETCH ur.role r
                        LEFT JOIN FETCH u.company
                        LEFT JOIN FETCH u.agency
                        WHERE u.email = :email
                        """)
        Optional<User> findByEmailFetchAll(String email);

        @Query("""
                        SELECT DISTINCT u FROM User u
                        LEFT JOIN FETCH u.userRoles ur
                        LEFT JOIN FETCH ur.role r
                        LEFT JOIN FETCH u.company
                        LEFT JOIN FETCH u.agency
                        WHERE u.id = :id
                        """)
        Optional<User> findByIdFetchAll(UUID id);

        Optional<User> findByEmail(String email);

        boolean existsByEmail(String email);

        Optional<User> findByPhone(String phone);

        boolean existsByPhone(String phone);

        Optional<User> findByIdAndCompanyId(UUID id, UUID companyId);

        Optional<User> findByEmailOrPhone(String email, String phone);

}
