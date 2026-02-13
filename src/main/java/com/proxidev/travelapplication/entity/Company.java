package com.proxidev.travelapplication.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.proxidev.travelapplication.enums.CompanyStatus;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Builder
@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

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
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<User> users = new ArrayList<>();

    @OneToMany(mappedBy = "company")
    @Builder.Default
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<Agency> agencies = new ArrayList<>();

    @OneToMany(mappedBy = "company")
    @Builder.Default
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<Role> roles = new ArrayList<>();

    @OneToMany(mappedBy = "company")
    @Builder.Default
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<Bus> buses = new ArrayList<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false, updatable = true, insertable = true)
    private LocalDateTime modifiedAt;
}