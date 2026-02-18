package com.proxidev.travelapplication.services;

import com.proxidev.travelapplication.entity.User;
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
    public User createUser(User user, String rawPassword) {
        if (userRepository.existsByEmail(user.getEmail()))
            throw new BusinessException("Un utilisateur avec cet email existe déjà", HttpStatus.CONFLICT);

        user.setPassword(passwordEncoder.encode(rawPassword));
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
        if (user.getUserRoles() == null)
            return Collections.emptyList();
        return user.getUserRoles().stream()
                .filter(ur -> ur.getRole() != null)
                .map(ur -> ur.getRole().getName()).toList();
    }
}