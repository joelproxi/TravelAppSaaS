package com.proxidev.travelapplication.services;

import com.proxidev.travelapplication.config.TenantProperties;
import com.proxidev.travelapplication.dtos.request.LoginRequest;
import com.proxidev.travelapplication.dtos.request.RefreshTokenRequest;
import com.proxidev.travelapplication.dtos.request.RegisterCompanyRequest;
import com.proxidev.travelapplication.dtos.request.RegisterTravelerRequest;
import com.proxidev.travelapplication.dtos.response.MessageResponse;
import com.proxidev.travelapplication.dtos.response.TokenResponse;
import com.proxidev.travelapplication.entity.*;
import com.proxidev.travelapplication.enums.CompanyStatus;
import com.proxidev.travelapplication.enums.UserType;
import com.proxidev.travelapplication.exception.BusinessException;
import com.proxidev.travelapplication.mappers.CompanyMapper;
import com.proxidev.travelapplication.mappers.UserMapper;
import com.proxidev.travelapplication.repository.*;
import com.proxidev.travelapplication.security.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserService userService;
    private final JwtTokenProvider jwt;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepo;
    private final CompanyRepository companyRepo;
    private final RoleRepository roleRepo;
    private final PermissionRepository permissionRepo;
    private final UserRoleRepository userRoleRepo;
    private final UserRepository userRepo;
    private final TenantProperties tenantProps;
    private final UserMapper userMapper;
    private final CompanyMapper companyMapper;

    // ================================================================
    // INSCRIPTION VOYAGEUR
    // ================================================================
    @Transactional
    public TokenResponse registerTraveler(RegisterTravelerRequest req) {
        User user = userMapper.toEntity(req, UserType.TRAVELER);
        user = userService.createUser(user, req.getPassword());
        return generateTokens(user, null, null);
    }

    // ================================================================
    // DEMANDE CRÉATION COMPAGNIE
    // ================================================================
    @Transactional
    public MessageResponse registerCompanyRequest(RegisterCompanyRequest req) {
        String slug = req.getCompanySlug().toLowerCase();
        if (companyRepo.existsBySlug(slug))
            throw new BusinessException("Le sous-domaine '" + slug + "' est déjà pris", HttpStatus.CONFLICT);

        User user = userMapper.toEntity(req, UserType.COMPANY_ADMIN);
        user = userService.createUser(user, req.getPassword());

        Company company = companyMapper.toEntity(req, slug, user.getId());
        company = companyRepo.save(company);

        user.setCompany(company);
        userRepo.save(user);

        return MessageResponse.builder()
                .message("Demande soumise. En attente de validation.")
                .data(Map.of("companyId", company.getId(), "slug", slug,
                        "domain", slug + "." + tenantProps.getBaseDomain()))
                .build();
    }

    // ================================================================
    // VALIDATION COMPAGNIE (SUPER ADMIN)
    // ================================================================
    @Transactional
    public MessageResponse approveCompany(UUID companyId) {
        Company company = companyRepo.findById(companyId)
                .orElseThrow(() -> new BusinessException("Compagnie introuvable", HttpStatus.NOT_FOUND));
        if (company.getStatus() != CompanyStatus.PENDING)
            throw new BusinessException("Compagnie déjà traitée");

        company.setStatus(CompanyStatus.APPROVED);
        companyRepo.save(company);

        List<Permission> allPerms = permissionRepo.findAll();
        Role adminRole = Role.builder()
                .name("Admin").description("Administrateur — tous les droits")
                .company(company).system(false).permissions(allPerms)
                .build();
        adminRole = roleRepo.save(adminRole);

        userRoleRepo.save(UserRole.builder()
                .user(User.builder()
                        .id(company.getRequestedByUserId()).build())
                .role(adminRole).build());

        log.info("Compagnie '{}' approuvée (slug: {})", company.getName(), company.getSlug());
        return new MessageResponse("Compagnie \"" + company.getName() + "\" approuvée. " +
                "Accessible via: " + company.getSlug() + "." + tenantProps.getBaseDomain());
    }

    @Transactional
    public MessageResponse rejectCompany(UUID companyId) {
        Company company = companyRepo.findById(companyId)
                .orElseThrow(() -> new BusinessException("Compagnie introuvable", HttpStatus.NOT_FOUND));
        company.setStatus(CompanyStatus.REJECTED);
        companyRepo.save(company);
        return new MessageResponse("Compagnie \"" + company.getName() + "\" rejetée");
    }

    // ================================================================
    // CONNEXION
    // ================================================================
    @Transactional
    public TokenResponse login(LoginRequest req, String userAgent, String ip) {
        User user = userService.findByEmailWithRoles(req.getEmail());
        if (user == null)
            throw new BusinessException("Email ou mot de passe incorrect", HttpStatus.UNAUTHORIZED);
        if (!user.isActive())
            throw new BusinessException("Compte désactivé", HttpStatus.UNAUTHORIZED);
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword()))
            throw new BusinessException("Email ou mot de passe incorrect", HttpStatus.UNAUTHORIZED);
        if (user.getUserType() == UserType.COMPANY_ADMIN && user.getCompany() != null
                && user.getCompany().getStatus() != CompanyStatus.APPROVED)
            throw new BusinessException("Compagnie en attente de validation", HttpStatus.FORBIDDEN);

        return generateTokens(user, userAgent, ip);
    }

    // ================================================================
    // REFRESH TOKENS
    // ================================================================
    @Transactional
    public TokenResponse refreshTokens(RefreshTokenRequest req, String userAgent, String ip) {
        if (!jwt.validateRefreshToken(req.getRefreshToken()))
            throw new BusinessException("Refresh token invalide", HttpStatus.UNAUTHORIZED);

        Claims claims = jwt.parseRefreshToken(req.getRefreshToken());
        UUID userId = jwt.getUserId(claims);
        String jti = jwt.getJti(claims);

        User user = userService.findByIdWithRoles(userId);
        if (!user.isActive())
            throw new BusinessException("Compte désactivé", HttpStatus.UNAUTHORIZED);

        List<RefreshToken> stored = refreshTokenRepo.findByUserIdAndRevokedFalse(userId);
        RefreshToken valid = null;
        for (RefreshToken rt : stored) {
            if (passwordEncoder.matches(jti, rt.getTokenHash())
                    && rt.getExpiresAt().isAfter(LocalDateTime.now())) {
                valid = rt;
                break;
            }
        }

        if (valid == null) {
            refreshTokenRepo.revokeAllByUserId(userId);
            log.warn("Refresh token invalide pour user {} — tous révoqués", userId);
            throw new BusinessException("Refresh token invalide — tous les tokens révoqués", HttpStatus.UNAUTHORIZED);
        }

        valid.setRevoked(true);
        refreshTokenRepo.save(valid);

        return generateTokens(user, userAgent, ip);
    }

    // ================================================================
    // DÉCONNEXION
    // ================================================================
    @Transactional
    public MessageResponse logout(UUID userId, String refreshTokenJwt) {
        if (refreshTokenJwt != null && !refreshTokenJwt.isBlank()) {
            try {
                if (jwt.validateRefreshToken(refreshTokenJwt)) {
                    Claims claims = jwt.parseRefreshToken(refreshTokenJwt);
                    String jti = jwt.getJti(claims);
                    for (RefreshToken rt : refreshTokenRepo.findByUserIdAndRevokedFalse(userId)) {
                        if (passwordEncoder.matches(jti, rt.getTokenHash())) {
                            rt.setRevoked(true);
                            refreshTokenRepo.save(rt);
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                log.warn("Erreur lors de la révocation: {}", e.getMessage());
            }
        } else {
            refreshTokenRepo.revokeAllByUserId(userId);
        }
        return new MessageResponse("Déconnexion réussie");
    }

    @Transactional
    public MessageResponse logoutAll(UUID userId) {
        refreshTokenRepo.revokeAllByUserId(userId);
        return new MessageResponse("Déconnexion de tous les appareils réussie");
    }

    // ================================================================
    // PRIVÉ
    // ================================================================
    private TokenResponse generateTokens(User user, String userAgent, String ip) {
        User full = userService.findByIdWithRoles(user.getId());
        List<String> permissions = userService.extractPermissions(full);
        List<String> roles = userService.extractRoleNames(full);

        String accessToken = jwt.generateAccessToken(
                full.getId(),
                full.getEmail(),
                full.getPhone(),
                full.getUserType(),
                full.getCompanyId(),
                full.getAgencyId(),
                roles,
                permissions);

        String jti = UUID.randomUUID().toString();
        String refreshToken = jwt.generateRefreshToken(full.getId(), jti);

        refreshTokenRepo.save(RefreshToken.builder()
                .tokenHash(passwordEncoder.encode(jti))
                .user(full)
                .expiresAt(LocalDateTime.now().plusSeconds(jwt.getRefreshExpirationMs() / 1000))
                .revoked(false).userAgent(userAgent).ipAddress(ip)
                .build());

        refreshTokenRepo.deleteExpiredByUserId(full.getId(), LocalDateTime.now());

        return TokenResponse.builder()
                .accessToken(accessToken).refreshToken(refreshToken)
                .tokenType("Bearer").expiresIn(jwt.getAccessExpirationMs() / 1000)
                .user(userMapper.toUserInfoResponse(full, roles, permissions))
                .build();
    }
}