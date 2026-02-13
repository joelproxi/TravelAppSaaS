package com.proxidev.travelapplication.controllers;


import com.proxidev.travelapplication.dtos.request.LoginRequest;
import com.proxidev.travelapplication.dtos.request.LogoutRequest;
import com.proxidev.travelapplication.dtos.request.RegisterCompanyRequest;
import com.proxidev.travelapplication.dtos.request.RegisterTravelerRequest;
import com.proxidev.travelapplication.dtos.response.MessageResponse;
import com.proxidev.travelapplication.dtos.response.TokenResponse;
import com.proxidev.travelapplication.security.CustomUserDetails;
import com.proxidev.travelapplication.services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register/traveler")
    @ResponseStatus(HttpStatus.CREATED)
    public TokenResponse registerTraveler(@Valid @RequestBody RegisterTravelerRequest req) {
        return authService.registerTraveler(req);
    }

    @PostMapping("/register/company")
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponse registerCompany(@Valid @RequestBody RegisterCompanyRequest req) {
        return authService.registerCompanyRequest(req);
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest req, HttpServletRequest http) {
        return authService.login(req, http.getHeader("User-Agent"), http.getRemoteAddr());
    }

    @PostMapping("/refresh")
    public TokenResponse refresh(@Valid @RequestBody RefreshTokenRequest req, HttpServletRequest http) {
        return authService.refreshTokens(req, http.getHeader("User-Agent"), http.getRemoteAddr());
    }

    @PostMapping("/logout")
    public MessageResponse logout(@AuthenticationPrincipal CustomUserDetails user,
                                  @RequestBody(required = false) LogoutRequest req) {
        String rt = req != null ? req.getRefreshToken() : null;
        return authService.logout(user.getUserId(), rt);
    }

    @PostMapping("/logout-all")
    public MessageResponse logoutAll(@AuthenticationPrincipal CustomUserDetails user) {
        return authService.logoutAll(user.getUserId());
    }

    @GetMapping("/me")
    public Map<String, Object> me(@AuthenticationPrincipal CustomUserDetails user) {
        return Map.of(
                "userId", user.getUserId(),
                "email", user.getEmail(),
                "userType", user.getUserType(),
                "companyId", user.getCompanyId() != null ? user.getCompanyId() : "null",
                "agencyId", user.getAgencyId() != null ? user.getAgencyId() : "null",
                "roles", user.getRoles(),
                "permissions", user.getPermissions()
        );
    }
}