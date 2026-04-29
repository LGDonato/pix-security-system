package com.lucasdonato.pixsecurity.customer.service;

import com.lucasdonato.pixsecurity.customer.dto.CustomerRequest;
import com.lucasdonato.pixsecurity.customer.dto.CustomerResponse;
import com.lucasdonato.pixsecurity.customer.entity.Customer;
import com.lucasdonato.pixsecurity.customer.repository.CustomerRepository;
import com.lucasdonato.pixsecurity.shared.exception.DuplicateResourceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Camada de regra de negocio: valida regras antes de acessar o banco via repository.
@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    // A transacao garante consistencia entre a validacao e a persistencia do cliente.
    @Transactional
    public CustomerResponse create(CustomerRequest request) {
        // Regra de negocio: nao permite cadastrar dois clientes com o mesmo CPF.
        if (customerRepository.existsByCpf(request.cpf())) {
            throw new DuplicateResourceException("CPF already registered");
        }

        Customer customer = new Customer(
                request.cpf(),
                request.fullName(),
                request.email(),
                request.phone()
        );

        return CustomerResponse.from(customerRepository.save(customer));
    }
}
