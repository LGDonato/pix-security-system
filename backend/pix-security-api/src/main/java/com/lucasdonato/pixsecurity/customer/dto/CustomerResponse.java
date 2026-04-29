// Define o pacote dos DTOs do módulo customer.
package com.lucasdonato.pixsecurity.customer.dto;

// Importa a entidade Customer para montar a resposta a partir do objeto salvo.
import com.lucasdonato.pixsecurity.customer.entity.Customer;
// LocalDateTime representa data e hora sem fuso horário.
import java.time.LocalDateTime;
// UUID representa o identificador único do cliente.
import java.util.UUID;

// Record usado como DTO de saída, representando o JSON devolvido pela API.
public record CustomerResponse(
        // Identificador único do cliente.
        UUID id,
        // CPF do cliente.
        String cpf,
        // Nome completo do cliente.
        String fullName,
        // E-mail do cliente, quando informado.
        String email,
        // Telefone do cliente, quando informado.
        String phone,
        // Status do cliente, podendo ser ACTIVE ou INACTIVE.
        Customer.Status status,
        // Data e hora de criação do registro.
        LocalDateTime createdAt,
        // Data e hora da última atualização do registro.
        LocalDateTime updatedAt
) {

    // Método de fábrica que converte uma entidade Customer em um DTO CustomerResponse.
    public static CustomerResponse from(Customer customer) {
        // Cria um DTO contendo os dados que a API deve devolver ao cliente HTTP.
        return new CustomerResponse(
                // Copia o id da entidade.
                customer.getId(),
                // Copia o CPF da entidade.
                customer.getCpf(),
                // Copia o nome completo da entidade.
                customer.getFullName(),
                // Copia o e-mail da entidade.
                customer.getEmail(),
                // Copia o telefone da entidade.
                customer.getPhone(),
                // Copia o status da entidade.
                customer.getStatus(),
                // Copia a data de criação da entidade.
                customer.getCreatedAt(),
                // Copia a data da última atualização da entidade.
                customer.getUpdatedAt()
        );
    }
}
