package com.lucasdonato.pixsecurity.customer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// DTO de entrada do PUT /customers/{id}; CPF nao entra aqui porque nao pode ser alterado.
@Schema(description = "Dados para atualizacao de cliente. O CPF nao e enviado porque nao pode ser alterado.")
public record CustomerUpdateRequest(
        @Schema(description = "Nome completo atualizado do cliente.", example = "Lucas Donato Silva")
        @NotBlank
        String fullName,

        @Schema(description = "E-mail atualizado do cliente.", example = "lucas.silva@example.com")
        @Email
        String email,

        @Schema(description = "Telefone atualizado do cliente.", example = "11999998888")
        String phone
) {
}
