package com.proxidev.travelapplication.controllers;

import com.proxidev.travelapplication.dtos.response.MessageResponse;
import com.proxidev.travelapplication.entity.Company;
import com.proxidev.travelapplication.enums.UserType;
import com.proxidev.travelapplication.security.CustomUserDetails;
import com.proxidev.travelapplication.services.AuthService;
import com.proxidev.travelapplication.services.CompanyService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;
    private final AuthService authService;

    @GetMapping("/pending")
    public List<Company> getPending(@AuthenticationPrincipal CustomUserDetails user) {
        requireSuperAdmin(user);
        return companyService.getPendingCompanies();
    }

    @PostMapping("/{companyId}/approve")
    public MessageResponse approve(@PathVariable UUID companyId,
            @AuthenticationPrincipal CustomUserDetails user) {
        requireSuperAdmin(user);
        return authService.approveCompany(companyId);
    }

    @PostMapping("/{companyId}/reject")
    public MessageResponse reject(@PathVariable UUID companyId,
            @AuthenticationPrincipal CustomUserDetails user) {
        requireSuperAdmin(user);
        return authService.rejectCompany(companyId);
    }

    @GetMapping("/public/all")
    public List<Company> getApproved() {
        return companyService.getApprovedCompanies();
    }

    private void requireSuperAdmin(CustomUserDetails user) {
        if (user.getUserType() != UserType.SUPER_ADMIN)
            throw new AccessDeniedException("Réservé au Super Administrateur");
    }
}