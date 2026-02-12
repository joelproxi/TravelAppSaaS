package com.proxidev.travelapplication.services;


import com.proxidev.travelapplication.entity.User;
import com.proxidev.travelapplication.enums.UserType;
import com.proxidev.travelapplication.exception.BusinessException;
import com.proxidev.travelapplication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User createUser(String firstName, String lastName, String email,
                           String rawPassword, String phone, UserType userType) {
        if (userRepository.existsByEmail(email))
            throw new BusinessException("Un utilisateur avec cet email existe déjà", HttpStatus.CONFLICT);

        User user = User.builder()
                .firstName(firstName).lastName(lastName).email(email)
                .password(passwordEncoder.encode(rawPassword))
                .phone(phone).userType(userType).active(true)
                .build();
        return userRepository.save(user);
    }

    public User findByEmailWithRoles(String email) {
        return userRepository.findByEmailFetchAll(email).orElse(null);
    }

    public User findByIdWithRoles(UUID id) {
        return userRepository.findByIdFetchAll(id)
                .orElseThrow(() -> new BusinessException("Utilisateur introuvable", HttpStatus.NOT_FOUND));
    }

    public List<String> extractPermissions(User user) {
        Set<String> perms = new LinkedHashSet<>();
        if (user.getUserRoles() != null)
            user.getUserRoles().forEach(ur -> {
                if (ur.getRole() != null && ur.getRole().getPermissions() != null)
                    ur.getRole().getPermissions().forEach(p -> perms.add(p.getName()));
            });
        return new ArrayList<>(perms);
    }

    public List<String> extractRoleNames(User user) {
        if (user.getUserRoles() == null) return Collections.emptyList();
        return user.getUserRoles().stream()
                .filter(ur -> ur.getRole() != null)
                .map(ur -> ur.getRole().getName()).toList();
    }
}