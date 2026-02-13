package com.proxidev.travelapplication.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterCompanyRequest {
    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    @Email
    private String userEmail;

    @NotBlank
    @Size(min = 8)
    private String password;

    @NotBlank
    private String phone;

    @NotBlank
    private String companyName;

    @NotBlank @Pattern(regexp = "^[a-z0-9]+(-[a-z0-9]+)*$", message = "Slug invalide")
    private String companySlug;

    private String companyDescription;

    private String companyPhone;

    @Email
    private String companyEmail;
}