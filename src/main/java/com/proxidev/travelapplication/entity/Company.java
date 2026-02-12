package com.proxidev.travelapplication.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.proxidev.travelapplication.enums.CompanyStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Company extends BaseEntity{

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String slug;

    private String description;
    private String logo;
    private String phone;
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private CompanyStatus status = CompanyStatus.PENDING;

    // L'utilisateur qui a soumis la demande
    private UUID requestedByUserId;

    @OneToMany(mappedBy = "company")
    @Builder.Default
    private List<User> users = new ArrayList<>();

    @OneToMany(mappedBy = "company")
    @Builder.Default
    private List<Agency> agencies = new ArrayList<>();

    @OneToMany(mappedBy = "company")
    @Builder.Default
    private List<Role> roles = new ArrayList<>();

    @OneToMany(mappedBy = "company")
    @Builder.Default
    private List<Bus> buses = new ArrayList<>();
}