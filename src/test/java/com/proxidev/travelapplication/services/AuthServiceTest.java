package com.proxidev.travelapplication.services;

import com.proxidev.travelapplication.dtos.request.RegisterTravelerRequest;
import com.proxidev.travelapplication.dtos.response.TokenResponse;
import com.proxidev.travelapplication.entity.User;
import com.proxidev.travelapplication.enums.UserType;
import com.proxidev.travelapplication.repository.RefreshTokenRepository;
import com.proxidev.travelapplication.repository.RefreshTokenRepository;
import com.proxidev.travelapplication.security.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserService userService;
    @Mock
    private JwtTokenProvider jwt;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RefreshTokenRepository refreshTokenRepo;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("Devrait inscrire un voyageur et retourner des tokens")
    void registerTraveler_Success() {
        // Given
        RegisterTravelerRequest req = new RegisterTravelerRequest();
        req.setFirstName("John");
        req.setLastName("Doe");
        req.setEmail("john.doe@example.com");
        req.setPassword("Password123!");
        req.setPhone("0123456789");

        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .userType(UserType.TRAVELER)
                .build();

        // Mocking dependencies
        when(userService.createUser(anyString(), anyString(), anyString(), anyString(), anyString(),
                eq(UserType.TRAVELER)))
                .thenReturn(user);
        when(userService.findByIdWithRoles(userId)).thenReturn(user);
        when(userService.extractPermissions(any())).thenReturn(Collections.emptyList());
        when(userService.extractRoleNames(any())).thenReturn(Collections.emptyList());

        when(passwordEncoder.encode(anyString())).thenReturn("encoded-jti");

        when(jwt.generateAccessToken(any(), any(), any(), any(), any(), any(), anyList(), anyList()))
                .thenReturn("mock-access-token");
        when(jwt.generateRefreshToken(any(), anyString()))
                .thenReturn("mock-refresh-token");
        when(jwt.getAccessExpirationMs()).thenReturn(3600000L);
        when(jwt.getRefreshExpirationMs()).thenReturn(86400000L);

        // When
        TokenResponse response = authService.registerTraveler(req);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("mock-access-token");
        assertThat(response.getRefreshToken()).isEqualTo("mock-refresh-token");
        assertThat(response.getUser().getEmail()).isEqualTo("john.doe@example.com");
    }
}
