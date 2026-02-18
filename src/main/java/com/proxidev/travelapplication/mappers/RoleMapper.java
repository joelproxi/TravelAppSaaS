package com.proxidev.travelapplication.mappers;

import com.proxidev.travelapplication.dtos.request.CreateRoleRequest;
import com.proxidev.travelapplication.dtos.response.RoleResponse;
import com.proxidev.travelapplication.entity.Company;
import com.proxidev.travelapplication.entity.Permission;
import com.proxidev.travelapplication.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RoleMapper {

    private final PermissionMapper permissionMapper;

    public RoleResponse toRoleResponse(Role role) {
        if (role == null)
            return null;

        List<com.proxidev.travelapplication.dtos.response.PermissionResponse> perms = role.getPermissions() == null
                ? Collections.emptyList()
                : role.getPermissions().stream()
                        .map(permissionMapper::toPermissionResponse)
                        .toList();

        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .companyId(role.getCompanyId())
                .system(role.isSystem())
                .permissions(perms)
                .build();
    }

    public Role toEntity(CreateRoleRequest request, Company company, List<Permission> permissions) {
        if (request == null)
            return null;
        return Role.builder()
                .name(request.getName())
                .description(request.getDescription())
                .company(company)
                .system(false)
                .permissions(permissions)
                .build();
    }
}
