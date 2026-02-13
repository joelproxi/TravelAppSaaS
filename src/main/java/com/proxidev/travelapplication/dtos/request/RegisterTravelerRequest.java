package com.proxidev.travelapplication.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterTravelerRequest{
        @NotBlank
        private String firstName;

        @NotBlank
        private String lastName;

        @NotBlank @Email
        private String email;

        @NotBlank @Size(min = 8)
        private String password;

        @NotBlank @Size(min = 8)
        private String phone;

}
