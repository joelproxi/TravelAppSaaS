package com.proxidev.travelapplication.dtos.response;


import com.proxidev.travelapplication.enums.UserType;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoResponse {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private UserType userType;
    private UUID companyId;
    private UUID agencyId;
    private List<String> roles;
    private List<String> permissions;
}