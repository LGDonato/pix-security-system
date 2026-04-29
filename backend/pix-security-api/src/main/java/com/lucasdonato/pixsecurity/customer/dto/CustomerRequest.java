package com.lucasdonato.pixsecurity.customer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

// DTO de entrada usado no POST /customers. As anotacoes sao avaliadas pelo @Valid no controller.
@Schema(description = "Dados para cadastro de um novo cliente.")
public record CustomerRequest(
        @Schema(description = "CPF do cliente. Deve conter exatamente 11 digitos numericos.", example = "12345678901")
        @NotBlank
        @Pattern(regexp = "\\d{11}", message = "cpf must contain exactly 11 digits")
        String cpf,

        @Schema(description = "Nome completo do cliente.", example = "Lucas Donato")
        @NotBlank
        String fullName,

        @Schema(description = "E-mail do cliente.", example = "lucas.donato@example.com")
        @Email
        String email,

        @Schema(description = "Telefone do cliente.", example = "11987654321")
        String phone
) {
}
