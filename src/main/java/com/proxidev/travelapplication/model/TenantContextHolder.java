package com.proxidev.travelapplication.model;

import java.util.UUID;

public record TenantContextHolder(
        String slug,
        UUID id
) {

}
