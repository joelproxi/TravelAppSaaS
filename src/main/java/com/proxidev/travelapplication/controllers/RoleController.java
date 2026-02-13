package com.proxidev.travelapplication.controllers;


import com.proxidev.travelapplication.dtos.request.AssignRoleRequest;
import com.proxidev.travelapplication.dtos.request.CreateRoleRequest;
import com.proxidev.travelapplication.dtos.response.MessageResponse;
import com.proxidev.travelapplication.dtos.response.PermissionResponse;
import com.proxidev.travelapplication.dtos.response.RoleResponse;
import com.proxidev.travelapplication.security.CustomUserDetails;
import com.proxidev.travelapplication.security.RequirePermission;
import com.proxidev.travelapplication.services.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping("/permissions")
    @RequirePermission({"role:read"})
    public List<PermissionResponse> getAllPermissions() {
        return roleService.getAllPermissions();
    }

    @GetMapping
    @RequirePermission({"role:read"})
    public List<RoleResponse> getCompanyRoles(@AuthenticationPrincipal CustomUserDetails user) {
        return roleService.getCompanyRoles(user.getCompanyId());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @RequirePermission({"role:create"})
    public RoleResponse createRole(@Valid @RequestBody CreateRoleRequest req,
                                   @AuthenticationPrincipal CustomUserDetails user) {
        return roleService.createRole(req, user.getCompanyId());
    }

    @PutMapping("/{roleId}")
    @RequirePermission({"role:update"})
    public RoleResponse updateRole(@PathVariable int roleId,
                                   @Valid @RequestBody CreateRoleRequest req,
                                   @AuthenticationPrincipal CustomUserDetails user) {
        return roleService.updateRole(roleId, user.getCompanyId(), req);
    }

    @DeleteMapping("/{roleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequirePermission({"role:delete"})
    public void deleteRole(@PathVariable int roleId,
                           @AuthenticationPrincipal CustomUserDetails user) {
        roleService.deleteRole(roleId, user.getCompanyId());
    }

    @PostMapping("/assign")
    @ResponseStatus(HttpStatus.CREATED)
    @RequirePermission({"role:assign"})
    public MessageResponse assignRole(@Valid @RequestBody AssignRoleRequest req,
                                      @AuthenticationPrincipal CustomUserDetails user) {
        return roleService.assignRole(req, user.getCompanyId());
    }

    @DeleteMapping("/assign/{userId}/{roleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequirePermission({"role:assign"})
    public void removeRole(@PathVariable UUID userId, @PathVariable int roleId,
                           @AuthenticationPrincipal CustomUserDetails user) {
        roleService.removeRole(userId, roleId, user.getCompanyId());
    }

    @GetMapping("/user/{userId}")
    @RequirePermission({"role:read"})
    public List<RoleResponse> getUserRoles(@PathVariable UUID userId,
                                           @AuthenticationPrincipal CustomUserDetails user) {
        return roleService.getUserRoles(userId, user.getCompanyId());
    }
}