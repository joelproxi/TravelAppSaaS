package com.proxidev.travelapplication.dtos.response;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleResponse {
    private UUID id;
    private String name;
    private String description;
    private UUID companyId;
    private boolean system;
    private List<PermissionResponse> permissions;
}