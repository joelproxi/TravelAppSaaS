package com.proxidev.travelapplication.dtos.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionResponse {
    private UUID id;
    private String name;
    private String description;
    private String module;
}