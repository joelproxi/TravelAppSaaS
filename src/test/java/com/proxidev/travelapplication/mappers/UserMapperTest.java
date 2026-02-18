package com.proxidev.travelapplication.mappers;

import com.proxidev.travelapplication.dtos.response.UserInfoResponse;
import com.proxidev.travelapplication.entity.User;
import com.proxidev.travelapplication.enums.UserType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();
    }

    @Test
    void toUserInfoResponse_ShouldMapCorrectly() {
        // Given
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .userType(UserType.TRAVELER)
                .build();
        List<String> roles = List.of("ROLE_USER");
        List<String> permissions = List.of("READ_DASHBOARD");

        // When
        UserInfoResponse response = userMapper.toUserInfoResponse(user, roles, permissions);

        // Then
        assertNotNull(response);
        assertEquals(userId, response.getId());
        assertEquals("John", response.getFirstName());
        assertEquals("Doe", response.getLastName());
        assertEquals("john.doe@example.com", response.getEmail());
        assertEquals(UserType.TRAVELER, response.getUserType());
        assertEquals(roles, response.getRoles());
        assertEquals(permissions, response.getPermissions());
    }
}
