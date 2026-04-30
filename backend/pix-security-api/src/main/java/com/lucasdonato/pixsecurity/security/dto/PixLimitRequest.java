package com.lucasdonato.pixsecurity.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Dados para criar ou atualizar os limites PIX de um cliente.")
public record PixLimitRequest(
        @Schema(description = "UUID do cliente no MySQL, armazenado como texto no MongoDB.", example = "550e8400-e29b-41d4-a716-446655440000")
        @NotBlank
        String customerId,

        @Schema(description = "Limite maximo por transacao PIX em centavos.", example = "100000")
        @NotNull
        @Positive
        Long transactionLimitCents,

        @Schema(description = "Limite diario PIX em centavos.", example = "500000")
        @NotNull
        @Positive
        Long dailyLimitCents,

        @Schema(description = "Limite PIX noturno em centavos.", example = "100000")
        @NotNull
        @Positive
        Long nighttimeLimitCents
) {
}
