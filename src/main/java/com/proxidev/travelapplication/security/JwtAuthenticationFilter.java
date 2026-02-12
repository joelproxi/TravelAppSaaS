package com.proxidev.travelapplication.security;


import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String jwt = extractJwt(request);

        if (StringUtils.hasText(jwt) && jwtTokenProvider.validateAccessToken(jwt)) {
            try {
                Claims claims = jwtTokenProvider.parseAccessToken(jwt);
                CustomUserDetails userDetails = new CustomUserDetails(
                        jwtTokenProvider.getUserId(claims),
                        jwtTokenProvider.getEmail(claims),
                        jwtTokenProvider.getPhone(claims),
                        "",
                        jwtTokenProvider.getUserType(claims),
                        jwtTokenProvider.getCompanyId(claims),
                        jwtTokenProvider.getAgencyId(claims),
                        jwtTokenProvider.getRoles(claims),
                        jwtTokenProvider.getPermissions(claims),
                        true
                );

                var auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (Exception e) {
                log.error("Impossible de définir l'authentification: {}", e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractJwt(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer "))
            return bearer.substring(7);
        return null;
    }
}