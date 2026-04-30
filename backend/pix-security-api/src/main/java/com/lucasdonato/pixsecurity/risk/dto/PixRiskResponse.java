package com.lucasdonato.pixsecurity.risk.dto;

import com.lucasdonato.pixsecurity.risk.model.RiskLevel;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resultado da validacao de risco de uma transacao PIX simulada.")
public record PixRiskResponse(
        @Schema(description = "Indica se a transacao pode seguir.", example = "true")
        boolean approved,

        @Schema(description = "Nivel de risco calculado para a transacao.", example = "LOW")
        RiskLevel riskLevel,

        @Schema(description = "Motivo da decisao de risco.", example = "Transacao aprovada com baixo risco")
        String reason
) {
}
