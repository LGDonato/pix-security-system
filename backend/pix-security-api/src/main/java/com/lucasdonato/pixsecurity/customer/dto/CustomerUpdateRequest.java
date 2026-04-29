package com.lucasdonato.pixsecurity.customer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// DTO de entrada do PUT /customers/{id}; CPF nao entra aqui porque nao pode ser alterado.
public record CustomerUpdateRequest(
        @NotBlank
        String fullName,

        @Email
        String email,

        String phone
) {
}
