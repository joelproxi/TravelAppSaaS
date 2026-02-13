package com.proxidev.travelapplication.dtos.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateRoleRequest {
    @NotBlank
    private String name;
    private String description;
    @NotEmpty
    private List<Integer> permissionIds;
}