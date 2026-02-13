package com.proxidev.travelapplication.seed;

import com.proxidev.travelapplication.entity.Permission;
import com.proxidev.travelapplication.entity.Role;
import com.proxidev.travelapplication.entity.User;
import com.proxidev.travelapplication.entity.UserRole;
import com.proxidev.travelapplication.enums.PermissionEnum;
import com.proxidev.travelapplication.enums.UserType;
import com.proxidev.travelapplication.repository.PermissionRepository;
import com.proxidev.travelapplication.repository.RoleRepository;
import com.proxidev.travelapplication.repository.UserRepository;
import com.proxidev.travelapplication.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@Order(1)
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final PermissionRepository permissionRepo;
    private final RoleRepository roleRepo;
    private final UserRepository userRepo;
    private final UserRoleRepository userRoleRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        seedPermissions();
        seedSuperAdmin();
        log.info("✅ Seeding terminé");
    }

    private void seedPermissions() {
        for (PermissionEnum pe : PermissionEnum.values()) {
            if (permissionRepo.findByName(pe.getCode()).isEmpty()) {
                permissionRepo.save(Permission.builder()
                        .name(pe.getCode())
                        .module(pe.getModule())
                        .description(pe.getDescription())
                        .build());
                log.info("Permission créée: {}", pe.getCode());
            }
        }
    }

    private void seedSuperAdmin() {
        String email = "superadmin@travelapp.com";
        if (userRepo.findByEmail(email).isPresent()) {
            log.info("Super Admin existe déjà");
            return;
        }

        // Rôle
        Role superRole = roleRepo.findByNameAndSystemTrue("SUPER_ADMIN")
                .orElseGet(() -> {
                    List<Permission> allPerms = permissionRepo.findAll();
                    return roleRepo.save(Role.builder()
                            .name("SUPER_ADMIN")
                            .description("Super Administrateur de la plateforme")
                            .system(true)
                            .permissions(new ArrayList<>(allPerms))
                            .build());
                });

        // Utilisateur
        User admin = userRepo.save(User.builder()
                .firstName("Super").lastName("Admin")
                .email(email)
                .password(passwordEncoder.encode("SuperAdmin@2024!"))
                .phone("+237612345678")
                .userType(UserType.SUPER_ADMIN)
                .active(true).build());

        // Assignation
        userRoleRepo.save(UserRole.builder().user(admin).role(superRole).build());

        log.info("✅ Super Admin créé: {} / SuperAdmin@2024!", email);
    }
}