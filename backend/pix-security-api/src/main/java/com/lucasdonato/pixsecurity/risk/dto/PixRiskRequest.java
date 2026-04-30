package com.lucasdonato.pixsecurity.risk.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;

@Schema(description = "Dados para validar o risco de uma transacao PIX simulada.")
public record PixRiskRequest(
        @Schema(description = "UUID do cliente no MySQL.", example = "550e8400-e29b-41d4-a716-446655440000")
        @NotBlank
        String customerId,

        @Schema(description = "Valor da transacao PIX em centavos.", example = "50000")
        @NotNull
        @Positive
        Long amountCents,

        @Schema(description = "Identificador do dispositivo que iniciou a transacao.", example = "device-android-abc123")
        @NotBlank
        String deviceId,

        @Schema(description = "Data e hora da transacao simulada.", example = "2026-04-29T21:15:00")
        @NotNull
        LocalDateTime transactionDateTime
) {
}
