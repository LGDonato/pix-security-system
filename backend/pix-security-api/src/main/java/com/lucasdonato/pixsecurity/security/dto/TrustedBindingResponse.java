package com.lucasdonato.pixsecurity.security.dto;

import com.lucasdonato.pixsecurity.security.document.TrustedBinding;
import com.lucasdonato.pixsecurity.security.document.TrustedBindingType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Dados retornados pela API para representar um vinculo seguro PIX.")
public record TrustedBindingResponse(
        @Schema(description = "Identificador do vinculo no MongoDB.", example = "662fda43f3f7b25fbc4c1a10")
        String id,

        @Schema(description = "UUID do cliente no MySQL, armazenado como texto no MongoDB.", example = "550e8400-e29b-41d4-a716-446655440000")
        String customerId,

        @Schema(description = "Tipo do vinculo seguro PIX.", example = "DEVICE")
        TrustedBindingType type,

        @Schema(description = "Valor associado ao vinculo, como identificador de dispositivo, chave PIX ou conta.", example = "device-android-abc123")
        String value,

        @Schema(description = "Indica se o vinculo esta ativo.", example = "true")
        boolean active,

        @Schema(description = "Data e hora de criacao do vinculo.", example = "2026-04-29T12:00:00")
        LocalDateTime createdAt,

        @Schema(description = "Data e hora de expiracao do vinculo.", example = "2026-12-31T23:59:59")
        LocalDateTime expiresAt
) {

    public static TrustedBindingResponse from(TrustedBinding trustedBinding) {
        return new TrustedBindingResponse(
                trustedBinding.getId(),
                trustedBinding.getCustomerId(),
                trustedBinding.getType(),
                trustedBinding.getValue(),
                trustedBinding.isActive(),
                trustedBinding.getCreatedAt(),
                trustedBinding.getExpiresAt()
        );
    }
}
