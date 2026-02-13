package com.proxidev.travelapplication.security;

import com.proxidev.travelapplication.enums.UserType;
import com.proxidev.travelapplication.multitenancy.TenantContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.UUID;

@Aspect
@Component
@Slf4j
public class PermissionAspect {

    @Before("@annotation(requirePermission)")
    public void check(JoinPoint jp, RequirePermission requirePermission) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails user))
            throw new AccessDeniedException("Authentification requise");

        if (user.getUserType() == UserType.SUPER_ADMIN)
            return;

        var context = TenantContext.getTenantContextHolder();
        UUID tenantId = (context != null) ? context.id() : null;
        if (tenantId != null && user.getCompanyId() != null && !tenantId.equals(user.getCompanyId()))
            throw new AccessDeniedException("Vous ne pouvez pas accéder aux ressources d'une autre compagnie");

        String[] required = requirePermission.value();
        boolean hasAll = Arrays.stream(required).allMatch(user.getPermissions()::contains);
        if (!hasAll) {
            log.warn("Permissions insuffisantes pour {}: requis={}, possédées={}",
                    user.getEmail(), Arrays.toString(required), user.getPermissions());
            throw new AccessDeniedException("Permissions insuffisantes. Requis: " + Arrays.toString(required));
        }
    }
}