package com.proxidev.travelapplication.services;


import com.proxidev.travelapplication.dtos.request.AssignRoleRequest;
import com.proxidev.travelapplication.dtos.request.CreateRoleRequest;
import com.proxidev.travelapplication.dtos.response.MessageResponse;
import com.proxidev.travelapplication.dtos.response.PermissionResponse;
import com.proxidev.travelapplication.dtos.response.RoleResponse;
import com.proxidev.travelapplication.entity.*;
import com.proxidev.travelapplication.exception.BusinessException;
import com.proxidev.travelapplication.repository.PermissionRepository;
import com.proxidev.travelapplication.repository.RoleRepository;
import com.proxidev.travelapplication.repository.UserRepository;
import com.proxidev.travelapplication.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepo;
    private final PermissionRepository permissionRepo;
    private final UserRoleRepository userRoleRepo;
    private final UserRepository userRepo;

    public List<PermissionResponse> getAllPermissions() {
        return permissionRepo.findAllByOrderByModuleAscNameAsc().stream()
                .map(p -> PermissionResponse.builder()
                        .id(p.getId()).name(p.getName())
                        .description(p.getDescription()).module(p.getModule()).build())
                .toList();
    }

    public List<RoleResponse> getCompanyRoles(UUID companyId) {
        return roleRepo.findByCompanyIdOrderByCreatedAtAsc(companyId).stream()
                .map(this::toResponse).toList();
    }

    @Transactional
    public RoleResponse createRole(CreateRoleRequest req, UUID companyId) {
        roleRepo.findByNameAndCompanyId(req.getName(), companyId).ifPresent(r -> {
            throw new BusinessException("Un rôle \"" + req.getName() + "\" existe déjà");
        });

        List<Permission> perms = permissionRepo.findByIdIn(req.getPermissionIds());
        if (perms.size() != req.getPermissionIds().size())
            throw new BusinessException("Certaines permissions sont invalides");

        Role role = Role.builder()
                .name(req.getName()).description(req.getDescription())
                .company(Company.builder().id(companyId).build())
                .system(false).permissions(perms).build();
        return toResponse(roleRepo.save(role));
    }

    @Transactional
    public RoleResponse updateRole(Integer roleId, UUID companyId, CreateRoleRequest req) {
        Role role = roleRepo.findByIdAndCompanyId(roleId, companyId)
                .orElseThrow(() -> new BusinessException("Rôle introuvable", HttpStatus.NOT_FOUND));
        if (role.isSystem())
            throw new BusinessException("Impossible de modifier un rôle système", HttpStatus.FORBIDDEN);

        List<Permission> perms = permissionRepo.findByIdIn(req.getPermissionIds());
        role.setName(req.getName());
        role.setDescription(req.getDescription());
        role.setPermissions(perms);
        return toResponse(roleRepo.save(role));
    }

    @Transactional
    public void deleteRole(Integer roleId, UUID companyId) {
        Role role = roleRepo.findByIdAndCompanyId(roleId, companyId)
                .orElseThrow(() -> new BusinessException("Rôle introuvable", HttpStatus.NOT_FOUND));
        if (role.isSystem())
            throw new BusinessException("Impossible de supprimer un rôle système", HttpStatus.FORBIDDEN);
        roleRepo.delete(role);
    }

    @Transactional
    public MessageResponse assignRole(AssignRoleRequest req, UUID companyId) {
        roleRepo.findByIdAndCompanyId(req.getRoleId(), companyId)
                .orElseThrow(() -> new BusinessException("Rôle introuvable dans votre compagnie", HttpStatus.NOT_FOUND));
        userRepo.findByIdAndCompanyId(req.getUserId(), companyId)
                .orElseThrow(() -> new BusinessException("Utilisateur introuvable dans votre compagnie", HttpStatus.NOT_FOUND));
        if (userRoleRepo.existsByUserIdAndRoleId(req.getUserId(), req.getRoleId()))
            throw new BusinessException("Cet utilisateur possède déjà ce rôle");

        userRoleRepo.save(UserRole.builder()
                .user(User.builder().id(req.getUserId()).build())
                .role(Role.builder().id(req.getRoleId()).build())
                .build());
        return new MessageResponse("Rôle assigné avec succès");
    }

    @Transactional
    public void removeRole(UUID userId, UUID roleId, UUID companyId) {
        roleRepo.findByIdAndCompanyId(roleId, companyId)
                .orElseThrow(() -> new BusinessException("Rôle introuvable", HttpStatus.NOT_FOUND));
        UserRole ur = userRoleRepo.findByUserIdAndRoleId(userId, roleId)
                .orElseThrow(() -> new BusinessException("L'utilisateur n'a pas ce rôle", HttpStatus.NOT_FOUND));
        userRoleRepo.delete(ur);
    }

    public List<RoleResponse> getUserRoles(UUID userId, UUID companyId) {
        return userRoleRepo.findByUserIdFetchRolePermissions(userId).stream()
                .map(UserRole::getRole)
                .filter(r -> companyId.equals(r.getCompanyId()))
                .map(this::toResponse).toList();
    }

    private RoleResponse toResponse(Role role) {
        return RoleResponse.builder()
                .id(role.getId()).name(role.getName())
                .description(role.getDescription())
                .companyId(role.getCompanyId()).system(role.isSystem())
                .permissions(role.getPermissions() == null ? List.of() :
                        role.getPermissions().stream().map(p ->
                                PermissionResponse.builder()
                                        .id(p.getId()).name(p.getName())
                                        .description(p.getDescription()).module(p.getModule())
                                        .build()).toList())
                .build();
    }
}