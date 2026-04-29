package com.lucasdonato.pixsecurity.customer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CustomerRequest(
        @NotBlank
        @Pattern(regexp = "\\d{11}", message = "cpf must contain exactly 11 digits")
        String cpf,

        @NotBlank
        String fullName,

        @Email
        String email,

        String phone
) {
}
