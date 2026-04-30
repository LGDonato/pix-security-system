package com.lucasdonato.pixsecurity.security.controller;

import com.lucasdonato.pixsecurity.security.dto.TrustedBindingRequest;
import com.lucasdonato.pixsecurity.security.dto.TrustedBindingResponse;
import com.lucasdonato.pixsecurity.security.service.TrustedBindingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/security/trusted-bindings")
@Tag(name = "Vinculos seguros PIX", description = "Endpoints para gerenciamento de vinculos seguros usados na seguranca PIX")
public class TrustedBindingController {

    private final TrustedBindingService trustedBindingService;

    public TrustedBindingController(TrustedBindingService trustedBindingService) {
        this.trustedBindingService = trustedBindingService;
    }

    @Operation(
            summary = "Cria um vinculo seguro PIX",
            description = "Registra um vinculo seguro de dispositivo, chave PIX ou conta para um cliente."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Vinculo seguro criado com sucesso"),
            @ApiResponse(responseCode = "409", description = "Vinculo confiavel ja cadastrado para este cliente")
    })
    @PostMapping
    public ResponseEntity<TrustedBindingResponse> create(@RequestBody TrustedBindingRequest request) {
        TrustedBindingResponse response = trustedBindingService.create(request);

        return ResponseEntity
                .created(URI.create("/security/trusted-bindings/" + response.id()))
                .body(response);
    }

    @Operation(
            summary = "Lista vinculos confiaveis por cliente",
            description = "Retorna os vinculos confiaveis cadastrados para um cliente."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vinculos confiaveis listados com sucesso")
    })
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<TrustedBindingResponse>> listByCustomerId(@PathVariable String customerId) {
        return ResponseEntity.ok(trustedBindingService.listByCustomerId(customerId));
    }

    @Operation(
            summary = "Remove vinculo confiavel",
            description = "Remove fisicamente um vinculo confiavel pelo identificador."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Vinculo confiavel removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Vinculo confiavel nao encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        trustedBindingService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
