package com.proxidev.travelapplication.mappers;

import com.proxidev.travelapplication.dtos.request.RegisterCompanyRequest;
import com.proxidev.travelapplication.entity.Company;
import com.proxidev.travelapplication.enums.CompanyStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CompanyMapper {

    public Company toEntity(RegisterCompanyRequest request, String slug, UUID requestedByUserId) {
        if (request == null)
            return null;
        return Company.builder()
                .name(request.getCompanyName())
                .slug(slug)
                .description(request.getCompanyDescription())
                .phone(request.getCompanyPhone())
                .email(request.getCompanyEmail())
                .status(CompanyStatus.PENDING)
                .requestedByUserId(requestedByUserId)
                .build();
    }
}
