package com.proxidev.travelapplication.mappers;

import com.proxidev.travelapplication.dtos.response.PermissionResponse;
import com.proxidev.travelapplication.entity.Permission;
import org.springframework.stereotype.Component;

@Component
public class PermissionMapper {

    public PermissionResponse toPermissionResponse(Permission permission) {
        if (permission == null)
            return null;
        return PermissionResponse.builder()
                .id(permission.getId())
                .name(permission.getName())
                .description(permission.getDescription())
                .module(permission.getModule())
                .build();
    }
}
