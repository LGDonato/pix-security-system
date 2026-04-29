package com.lucasdonato.pixsecurity.customer.controller;

import com.lucasdonato.pixsecurity.customer.dto.CustomerRequest;
import com.lucasdonato.pixsecurity.customer.dto.CustomerResponse;
import com.lucasdonato.pixsecurity.customer.dto.CustomerUpdateRequest;
import com.lucasdonato.pixsecurity.customer.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// Camada de entrada HTTP: recebe a requisicao, valida o DTO e delega a regra para o service.
@RestController
@RequestMapping("/customers")
@Tag(name = "Clientes", description = "Endpoints para cadastro e manutencao de clientes")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    // POST /customers cria um cliente e retorna 201 Created com a URL do recurso criado.
    @Operation(summary = "Cria um cliente", description = "Cadastra um novo cliente com CPF unico.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cliente criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados invalidos na requisicao"),
            @ApiResponse(responseCode = "409", description = "CPF ja cadastrado")
    })
    @PostMapping
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CustomerRequest request) {
        // Fluxo da aplicacao: controller -> service -> repository -> banco.
        CustomerResponse response = customerService.create(request);

        return ResponseEntity
                .created(URI.create("/customers/" + response.id()))
                .body(response);
    }

    @Operation(summary = "Lista clientes", description = "Retorna todos os clientes cadastrados.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Clientes listados com sucesso")
    })
    @GetMapping
    public ResponseEntity<List<CustomerResponse>> listAll() {
        return ResponseEntity.ok(customerService.listAll());
    }

    @Operation(summary = "Busca cliente por ID", description = "Retorna os dados de um cliente pelo UUID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "404", description = "Cliente nao encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(customerService.findById(id));
    }

    @Operation(
            summary = "Atualiza cliente",
            description = "Atualiza os dados editaveis do cliente. O CPF nao pode ser alterado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados invalidos na requisicao"),
            @ApiResponse(responseCode = "404", description = "Cliente nao encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody CustomerUpdateRequest request
    ) {
        return ResponseEntity.ok(customerService.update(id, request));
    }

    @Operation(
            summary = "Inativa cliente",
            description = "Realiza soft delete: o cliente nao e removido fisicamente, apenas tem o status alterado para INACTIVE."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cliente inativado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente nao encontrado")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        customerService.inactivate(id);
    }
}
