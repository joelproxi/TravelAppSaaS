package com.proxidev.travelapplication.mappers;

import com.proxidev.travelapplication.dtos.request.RegisterCompanyRequest;
import com.proxidev.travelapplication.dtos.request.RegisterTravelerRequest;
import com.proxidev.travelapplication.dtos.response.UserInfoResponse;
import com.proxidev.travelapplication.entity.User;
import com.proxidev.travelapplication.enums.UserType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMapper {

    public UserInfoResponse toUserInfoResponse(User user, List<String> roles, List<String> permissions) {
        if (user == null)
            return null;

        return UserInfoResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .userType(user.getUserType())
                .companyId(user.getCompanyId())
                .agencyId(user.getAgencyId())
                .roles(roles)
                .permissions(permissions)
                .build();
    }

    public User toEntity(RegisterTravelerRequest request, UserType type) {
        if (request == null)
            return null;
        return User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .userType(type)
                .active(true)
                .build();
    }

    public User toEntity(RegisterCompanyRequest request, UserType type) {
        if (request == null)
            return null;
        return User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getUserEmail())
                .phone(request.getPhone())
                .userType(type)
                .active(true)
                .build();
    }
}
