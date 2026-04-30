package com.lucasdonato.pixsecurity.security.controller;

import com.lucasdonato.pixsecurity.security.dto.PixLimitRequest;
import com.lucasdonato.pixsecurity.security.dto.PixLimitResponse;
import com.lucasdonato.pixsecurity.security.service.PixLimitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/security/pix-limits")
@Tag(name = "Limites PIX", description = "Endpoints para gerenciamento de limites PIX por cliente")
public class PixLimitController {

    private final PixLimitService pixLimitService;

    public PixLimitController(PixLimitService pixLimitService) {
        this.pixLimitService = pixLimitService;
    }

    @Operation(
            summary = "Cria ou atualiza limites PIX",
            description = "Realiza upsert dos limites PIX ativos de um cliente."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Limites PIX criados ou atualizados com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados invalidos na requisicao")
    })
    @PostMapping
    public ResponseEntity<PixLimitResponse> upsert(@Valid @RequestBody PixLimitRequest request) {
        PixLimitResponse response = pixLimitService.upsert(request);

        return ResponseEntity
                .created(URI.create("/security/pix-limits/customer/" + response.customerId()))
                .body(response);
    }

    @Operation(
            summary = "Busca limites PIX ativos por cliente",
            description = "Retorna os limites PIX ativos cadastrados para um cliente."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Limites PIX encontrados"),
            @ApiResponse(responseCode = "404", description = "Limites PIX nao encontrados para este cliente")
    })
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<PixLimitResponse> findByCustomerId(@PathVariable String customerId) {
        return ResponseEntity.ok(pixLimitService.findActiveByCustomerId(customerId));
    }
}
