// Define o pacote dos DTOs do módulo customer.
package com.lucasdonato.pixsecurity.customer.dto;

// @Email valida o formato do e-mail quando o campo é informado.
import jakarta.validation.constraints.Email;
// @NotBlank valida que uma String não seja nula, vazia ou composta só por espaços.
import jakarta.validation.constraints.NotBlank;
// @Pattern valida uma String usando uma expressão regular.
import jakarta.validation.constraints.Pattern;

// Record usado como DTO de entrada, representando o JSON recebido para criar cliente.
public record CustomerRequest(
        // @NotBlank obriga o CPF a ser enviado na requisição.
        @NotBlank
        // @Pattern exige que o CPF tenha exatamente 11 dígitos numéricos.
        @Pattern(regexp = "\\d{11}", message = "cpf must contain exactly 11 digits")
        // Campo cpf recebido do JSON da requisição.
        String cpf,

        // @NotBlank obriga o nome completo a ser enviado na requisição.
        @NotBlank
        // Campo fullName recebido do JSON da requisição.
        String fullName,

        // @Email valida o formato do e-mail, mas o campo continua opcional.
        @Email
        // Campo email recebido do JSON da requisição.
        String email,

        // Campo phone recebido do JSON da requisição.
        String phone
) {
}
