package com.lucasdonato.pixsecurity.customer.dto;

import com.lucasdonato.pixsecurity.customer.entity.Customer;
import java.time.LocalDateTime;
import java.util.UUID;

// DTO de saida: define o contrato JSON retornado pela API sem expor a entidade diretamente.
public record CustomerResponse(
        UUID id,
        String cpf,
        String fullName,
        String email,
        String phone,
        Customer.Status status,
        LocalDateTime createdAt,
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
