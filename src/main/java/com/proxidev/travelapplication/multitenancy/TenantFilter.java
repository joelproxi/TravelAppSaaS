package com.proxidev.travelapplication.multitenancy;

import com.proxidev.travelapplication.config.TenantProperties;
import com.proxidev.travelapplication.entity.Company;
import com.proxidev.travelapplication.enums.CompanyStatus;
import com.proxidev.travelapplication.model.TenantContextHolder;
import com.proxidev.travelapplication.repository.CompanyRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
@Slf4j
public class TenantFilter extends OncePerRequestFilter {

    private final CompanyRepository companyRepository;
    private final TenantProperties tenantProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String host = request.getHeader("Host");
            if (host != null) {
                String hostWithoutPort = host.split(":")[0];
                String slug = extractSubdomain(hostWithoutPort);

                if (slug != null && !slug.isBlank()
                        && !tenantProperties.getIgnoredSubdomainsList().contains(slug.toLowerCase())) {

                    Optional<Company> companyOpt = companyRepository.findBySlug(slug.toLowerCase());

                    if (companyOpt.isEmpty()) {
                        sendError(response, 404, "Compagnie introuvable pour le domaine: " + slug);
                        return;
                    }

                    Company company = companyOpt.get();
                    if (company.getStatus() != CompanyStatus.APPROVED) {
                        sendError(response, 403, "Cette compagnie n'est pas encore activée");
                        return;
                    }

                    TenantContextHolder contextHolder = new TenantContextHolder(slug.toLowerCase(), company.getId());
                    TenantContext.setTenantContextHolder(contextHolder);
                    log.debug("Tenant résolu: {} → {}", slug, company.getId());
                }
            }

            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clearTenantContextHolder();
        }
    }

    private String extractSubdomain(String host) {
        if ("localhost".equals(host) || "127.0.0.1".equals(host)) return null;
        String suffix = "." + tenantProperties.getBaseDomain();
        if (host.endsWith(suffix)) {
            String sub = host.substring(0, host.length() - suffix.length());
            int dot = sub.indexOf('.');
            return dot != -1 ? sub.substring(0, dot) : sub;
        }
        return null;
    }

    private void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"" + message + "\"}");
    }
}