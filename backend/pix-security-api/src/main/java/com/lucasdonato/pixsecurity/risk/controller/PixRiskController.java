package com.lucasdonato.pixsecurity.risk.controller;

import com.lucasdonato.pixsecurity.risk.dto.PixRiskRequest;
import com.lucasdonato.pixsecurity.risk.dto.PixRiskResponse;
import com.lucasdonato.pixsecurity.risk.service.PixRiskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pix/risk")
@Tag(name = "Risco PIX", description = "Endpoints para validacao de risco de transacoes PIX simuladas")
public class PixRiskController {

    private final PixRiskService pixRiskService;

    public PixRiskController(PixRiskService pixRiskService) {
        this.pixRiskService = pixRiskService;
    }

    @Operation(
            summary = "Valida risco de transacao PIX",
            description = "Avalia cliente, limites PIX e dispositivo confiavel para uma transacao PIX simulada."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Risco da transacao avaliado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados invalidos na requisicao")
    })
    @PostMapping("/validate")
    public ResponseEntity<PixRiskResponse> validate(@Valid @RequestBody PixRiskRequest request) {
        return ResponseEntity.ok(pixRiskService.validate(request));
    }
}
