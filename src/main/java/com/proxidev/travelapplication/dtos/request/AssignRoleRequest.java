package com.proxidev.travelapplication.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssignRoleRequest {
    @NotNull
    private UUID userId;
    @NotNull private Integer roleId;
}