package com.lucasdonato.pixsecurity.security.controller;

import com.lucasdonato.pixsecurity.security.dto.TrustedBindingRequest;
import com.lucasdonato.pixsecurity.security.dto.TrustedBindingResponse;
import com.lucasdonato.pixsecurity.security.service.TrustedBindingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import org.springframework.http.ResponseEntity;
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
            @ApiResponse(responseCode = "201", description = "Vinculo seguro criado com sucesso")
    })
    @PostMapping
    public ResponseEntity<TrustedBindingResponse> create(@RequestBody TrustedBindingRequest request) {
        TrustedBindingResponse response = trustedBindingService.create(request);

        return ResponseEntity
                .created(URI.create("/security/trusted-bindings/" + response.id()))
                .body(response);
    }
}
