package com.lucasdonato.pixsecurity.customer.dto;

import com.lucasdonato.pixsecurity.customer.entity.Customer;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;

// DTO de saida: define o contrato JSON retornado pela API sem expor a entidade diretamente.
@Schema(description = "Dados retornados pela API para representar um cliente.")
public record CustomerResponse(
        @Schema(description = "Identificador unico do cliente.", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "CPF do cliente com 11 digitos numericos.", example = "12345678901")
        String cpf,

        @Schema(description = "Nome completo do cliente.", example = "Lucas Donato")
        String fullName,

        @Schema(description = "E-mail do cliente.", example = "lucas.donato@example.com")
        String email,

        @Schema(description = "Telefone do cliente.", example = "11987654321")
        String phone,

        @Schema(description = "Status do cliente. Pode ser ACTIVE ou INACTIVE.", example = "ACTIVE")
        Customer.Status status,

        @Schema(description = "Data e hora de criacao do cadastro.", example = "2026-04-29T12:00:00")
        LocalDateTime createdAt,

        @Schema(description = "Data e hora da ultima atualizacao do cadastro.", example = "2026-04-29T12:30:00")
        LocalDateTime updatedAt
) {

    public static CustomerResponse from(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getCpf(),
                customer.getFullName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getStatus(),
                customer.getCreatedAt(),
                customer.getUpdatedAt()
        );
    }
}
