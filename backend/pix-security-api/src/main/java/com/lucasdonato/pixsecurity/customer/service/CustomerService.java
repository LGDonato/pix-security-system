package com.lucasdonato.pixsecurity.customer.service;

import com.lucasdonato.pixsecurity.customer.dto.CustomerRequest;
import com.lucasdonato.pixsecurity.customer.dto.CustomerResponse;
import com.lucasdonato.pixsecurity.customer.dto.CustomerUpdateRequest;
import com.lucasdonato.pixsecurity.customer.entity.Customer;
import com.lucasdonato.pixsecurity.customer.repository.CustomerRepository;
import com.lucasdonato.pixsecurity.shared.exception.DuplicateResourceException;
import com.lucasdonato.pixsecurity.shared.exception.ResourceNotFoundException;
import java.util.List;
import java.util.UUID;
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

    @Transactional(readOnly = true)
    public List<CustomerResponse> listAll() {
        return customerRepository.findAll()
                .stream()
                .map(CustomerResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public CustomerResponse findById(UUID id) {
        return CustomerResponse.from(findCustomerById(id));
    }

    @Transactional
    public CustomerResponse update(UUID id, CustomerUpdateRequest request) {
        Customer customer = findCustomerById(id);

        customer.setFullName(request.fullName());
        customer.setEmail(request.email());
        customer.setPhone(request.phone());

        return CustomerResponse.from(customer);
    }

    @Transactional
    public void inactivate(UUID id) {
        Customer customer = findCustomerById(id);
        // Soft delete: mantem o historico no banco e impede perda de dados do cliente.
        customer.setStatus(Customer.Status.INACTIVE);
    }

    private Customer findCustomerById(UUID id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
    }
}
