// Define o pacote da camada service do módulo customer.
package com.lucasdonato.pixsecurity.customer.service;

// Importa o DTO com os dados recebidos da requisição.
import com.lucasdonato.pixsecurity.customer.dto.CustomerRequest;
// Importa o DTO com os dados que serão retornados pela API.
import com.lucasdonato.pixsecurity.customer.dto.CustomerResponse;
// Importa a entidade que representa um cliente no banco.
import com.lucasdonato.pixsecurity.customer.entity.Customer;
// Importa o repository, que é a camada responsável por acessar o banco.
import com.lucasdonato.pixsecurity.customer.repository.CustomerRepository;
// HttpStatus contém códigos HTTP, como 409 CONFLICT.
import org.springframework.http.HttpStatus;
// @Service marca esta classe como componente de regra de negócio do Spring.
import org.springframework.stereotype.Service;
// @Transactional controla a transação de banco durante a execução do método.
import org.springframework.transaction.annotation.Transactional;
// ResponseStatusException permite lançar uma exceção associada a um status HTTP.
import org.springframework.web.server.ResponseStatusException;

// @Service registra esta classe no contexto do Spring para injeção de dependência.
@Service
// Service fica entre controller e repository: recebe chamadas do controller e aplica regras de negócio.
public class CustomerService {

    // Repository usado para consultar e salvar clientes no banco de dados.
    private final CustomerRepository customerRepository;

    // Construtor usado pelo Spring para injetar automaticamente CustomerRepository.
    public CustomerService(CustomerRepository customerRepository) {
        // Guarda o repository recebido para uso nos métodos da classe.
        this.customerRepository = customerRepository;
    }

    // @Transactional faz as operações de banco deste método ocorrerem dentro de uma transação.
    @Transactional
    // Método responsável por criar um cliente seguindo as regras de negócio.
    public CustomerResponse create(CustomerRequest request) {
        // Consulta o banco para saber se já existe um cliente com o CPF informado.
        if (customerRepository.existsByCpf(request.cpf())) {
            // Se o CPF já existir, interrompe o cadastro e retorna erro HTTP 409 Conflict.
            throw new ResponseStatusException(HttpStatus.CONFLICT, "CPF already registered");
        }

        // Cria uma entidade Customer com os dados validados que vieram do DTO.
        Customer customer = new Customer(
                // Passa o CPF recebido para a entidade.
                request.cpf(),
                // Passa o nome completo recebido para a entidade.
                request.fullName(),
                // Passa o e-mail opcional recebido para a entidade.
                request.email(),
                // Passa o telefone opcional recebido para a entidade.
                request.phone()
        );

        // Fluxo: Service chama Repository para salvar no banco e converte a entidade salva em DTO de resposta.
        return CustomerResponse.from(customerRepository.save(customer));
    }
}
