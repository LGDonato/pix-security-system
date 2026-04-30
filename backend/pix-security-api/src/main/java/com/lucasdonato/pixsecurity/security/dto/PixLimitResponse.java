package com.lucasdonato.pixsecurity.security.dto;

import com.lucasdonato.pixsecurity.security.document.PixLimit;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Dados retornados pela API para representar os limites PIX de um cliente.")
public record PixLimitResponse(
        @Schema(description = "Identificador do limite PIX no MongoDB.", example = "662fda43f3f7b25fbc4c1a10")
        String id,

        @Schema(description = "UUID do cliente no MySQL, armazenado como texto no MongoDB.", example = "550e8400-e29b-41d4-a716-446655440000")
        String customerId,

        @Schema(description = "Limite maximo por transacao PIX em centavos.", example = "100000")
        Long transactionLimitCents,

        @Schema(description = "Limite diario PIX em centavos.", example = "500000")
        Long dailyLimitCents,

        @Schema(description = "Limite PIX noturno em centavos.", example = "100000")
        Long nighttimeLimitCents,

        @Schema(description = "Indica se o limite PIX esta ativo.", example = "true")
        boolean active,

        @Schema(description = "Data e hora de criacao do limite PIX.", example = "2026-04-29T12:00:00")
        LocalDateTime createdAt,

        @Schema(description = "Data e hora da ultima atualizacao do limite PIX.", example = "2026-04-29T12:30:00")
        LocalDateTime updatedAt
) {

    public static PixLimitResponse from(PixLimit pixLimit) {
        return new PixLimitResponse(
                pixLimit.getId(),
                pixLimit.getCustomerId(),
                pixLimit.getTransactionLimitCents(),
                pixLimit.getDailyLimitCents(),
                pixLimit.getNighttimeLimitCents(),
                pixLimit.isActive(),
                pixLimit.getCreatedAt(),
                pixLimit.getUpdatedAt()
        );
    }
}
