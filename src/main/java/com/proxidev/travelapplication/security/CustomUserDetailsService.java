package com.proxidev.travelapplication.security;

import com.proxidev.travelapplication.entity.User;
import com.proxidev.travelapplication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmailFetchAll(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable: " + email));
        return build(user);
    }

    public CustomUserDetails build(User user) {
        Set<String> roles = new LinkedHashSet<>();
        Set<String> permissions = new LinkedHashSet<>();
        if (user.getUserRoles() != null) {
            user.getUserRoles().forEach(ur -> {
                if (ur.getRole() != null) {
                    roles.add(ur.getRole().getName());
                    if (ur.getRole().getPermissions() != null)
                        ur.getRole().getPermissions().forEach(p -> permissions.add(p.getName()));
                }
            });
        }
        return new CustomUserDetails(user.getId(), user.getEmail(), user.getPhone(), user.getPassword(),
                user.getUserType(), user.getCompanyId(), user.getAgencyId(),
                new ArrayList<>(roles), new ArrayList<>(permissions), user.isActive());
    }
}