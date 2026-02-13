package com.proxidev.travelapplication.services;


import com.proxidev.travelapplication.entity.Company;
import com.proxidev.travelapplication.enums.CompanyStatus;
import com.proxidev.travelapplication.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepo;

    public List<Company> getPendingCompanies() {
        return companyRepo.findByStatusOrderByCreatedAtDesc(CompanyStatus.PENDING);
    }

    public List<Company> getApprovedCompanies() {
        return companyRepo.findByStatus(CompanyStatus.APPROVED);
    }
}