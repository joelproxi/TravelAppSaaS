package com.proxidev.travelapplication.security;


import com.proxidev.travelapplication.enums.UserType;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Getter
public class CustomUserDetails implements UserDetails {

    private final UUID userId;
    private final String email;
    private final String phone;
    private final String password;
    private final UserType userType;
    private final UUID companyId;
    private final UUID agencyId;
    private final List<String> roles;
    private final List<String> permissions;
    private final boolean active;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(UUID userId, String email, String phone, String password,
                             UserType userType, UUID companyId, UUID agencyId,
                             List<String> roles, List<String> permissions, boolean active) {
        this.userId = userId;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.userType = userType;
        this.companyId = companyId;
        this.agencyId = agencyId;
        this.roles = roles;
        this.permissions = permissions;
        this.active = active;

        List<GrantedAuthority> auths = new ArrayList<>();
        auths.add(new SimpleGrantedAuthority("ROLE_" + userType.name()));
        auths.addAll(permissions.stream().map(SimpleGrantedAuthority::new).toList());
        this.authorities = Collections.unmodifiableList(auths);
    }

    @Override public String getUsername() { return !Objects.equals(email, null) ? email : phone ; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return active; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return active; }
}
