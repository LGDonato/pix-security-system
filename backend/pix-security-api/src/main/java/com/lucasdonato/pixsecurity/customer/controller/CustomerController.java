// Define o pacote da camada controller do módulo customer.
package com.lucasdonato.pixsecurity.customer.controller;

// Importa o DTO que representa os dados recebidos no corpo da requisição.
import com.lucasdonato.pixsecurity.customer.dto.CustomerRequest;
// Importa o DTO que representa os dados enviados na resposta da API.
import com.lucasdonato.pixsecurity.customer.dto.CustomerResponse;
// Importa o serviço que contém a regra de negócio de clientes.
import com.lucasdonato.pixsecurity.customer.service.CustomerService;
// @Valid ativa a validação das anotações do DTO, como @NotBlank, @Pattern e @Email.
import jakarta.validation.Valid;
// URI representa o endereço do recurso criado, usado no header Location.
import java.net.URI;
// ResponseEntity permite controlar status HTTP, headers e corpo da resposta.
import org.springframework.http.ResponseEntity;
// @PostMapping mapeia requisições HTTP POST para um método.
import org.springframework.web.bind.annotation.PostMapping;
// @RequestBody transforma o JSON enviado no corpo da requisição em um objeto Java.
import org.springframework.web.bind.annotation.RequestBody;
// @RequestMapping define o caminho base dos endpoints deste controller.
import org.springframework.web.bind.annotation.RequestMapping;
// @RestController marca a classe como controller REST e serializa retornos como JSON.
import org.springframework.web.bind.annotation.RestController;

// @RestController indica que esta classe recebe requisições HTTP e retorna dados da API.
@RestController
// Define /customers como rota base; neste arquivo, os endpoints começam por /customers.
@RequestMapping("/customers")
// Controller é a entrada da aplicação: recebe HTTP e delega a regra para o service.
public class CustomerController {

    // Dependência da camada service, que contém a lógica de cadastro.
    private final CustomerService customerService;

    // Construtor usado pelo Spring para injetar automaticamente CustomerService.
    public CustomerController(CustomerService customerService) {
        // Guarda o service recebido para ser usado nos métodos do controller.
        this.customerService = customerService;
    }

    // @PostMapping cria o endpoint POST /customers.
    @PostMapping
    // Método chamado quando a API recebe uma requisição POST /customers.
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CustomerRequest request) {
        // Fluxo: Controller recebe o DTO validado e chama o Service para aplicar as regras.
        CustomerResponse response = customerService.create(request);

        // Monta uma resposta HTTP 201 Created com o cliente criado no corpo.
        return ResponseEntity
                // Define o header Location apontando para o recurso recém-criado.
                .created(URI.create("/customers/" + response.id()))
                // Define o corpo da resposta com os dados do CustomerResponse.
                .body(response);
    }
}
