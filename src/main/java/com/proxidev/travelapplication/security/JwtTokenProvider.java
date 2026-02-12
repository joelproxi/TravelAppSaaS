package com.proxidev.travelapplication.security;

import com.proxidev.travelapplication.enums.UserType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider {

    private final JwtProperties props;

    public String generateAccessToken(UUID userId, String email, String phone, UserType userType,
                                      UUID companyId, UUID agencyId,
                                      List<String> roles, List<String> permissions) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("phone", phone);
        claims.put("userType", userType.name());
        claims.put("companyId", companyId != null ? companyId.toString() : null);
        claims.put("agencyId", agencyId != null ? agencyId.toString() : null);
        claims.put("roles", roles);
        claims.put("permissions", permissions);
        claims.put("type", "access");

        return Jwts.builder()
                .subject(userId.toString())
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + props.getAccessExpirationMs()))
                .signWith(accessKey())
                .compact();
    }

    public String generateRefreshToken(UUID userId, String jti) {
        return Jwts.builder()
                .subject(userId.toString())
                .id(jti)
                .claim("type", "refresh")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + props.getRefreshExpirationMs()))
                .signWith(refreshKey())
                .compact();
    }

    public Claims parseAccessToken(String token) {
        return parse(token, accessKey());
    }

    public Claims parseRefreshToken(String token) {
        return parse(token, refreshKey());
    }

    public boolean validateAccessToken(String token) {
        return validate(token, accessKey());
    }

    public boolean validateRefreshToken(String token) {
        return validate(token, refreshKey());
    }

    // ---- Extraction helpers ----
    public UUID getUserId(Claims c) { return UUID.fromString(c.getSubject()); }
    public String getEmail(Claims c) { return c.get("email", String.class); }
    public String getPhone(Claims c) { return c.get("phone", String.class); }
    public UserType getUserType(Claims c) { return UserType.valueOf(c.get("userType", String.class)); }

    public UUID getCompanyId(Claims c) {
        String v = c.get("companyId", String.class);
        return v != null ? UUID.fromString(v) : null;
    }

    public UUID getAgencyId(Claims c) {
        String v = c.get("agencyId", String.class);
        return v != null ? UUID.fromString(v) : null;
    }

    @SuppressWarnings("unchecked")
    public List<String> getRoles(Claims c) {
        var r = c.get("roles", List.class);
        return r != null ? r : Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    public List<String> getPermissions(Claims c) {
        var p = c.get("permissions", List.class);
        return p != null ? p : Collections.emptyList();
    }

    public String getJti(Claims c) { return c.getId(); }

    public long getAccessExpirationMs() { return props.getAccessExpirationMs(); }
    public long getRefreshExpirationMs() { return props.getRefreshExpirationMs(); }

    // ---- Private ----
    private Claims parse(String token, SecretKey key) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }

    private boolean validate(String token, SecretKey key) {
        try { parse(token, key); return true; }
        catch (JwtException | IllegalArgumentException e) { log.warn("JWT invalide: {}", e.getMessage()); return false; }
    }

    private SecretKey accessKey() { return Keys.hmacShaKeyFor(props.getAccessSecret().getBytes(StandardCharsets.UTF_8)); }
    private SecretKey refreshKey() { return Keys.hmacShaKeyFor(props.getRefreshSecret().getBytes(StandardCharsets.UTF_8)); }
}