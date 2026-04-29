package com.lucasdonato.pixsecurity.customer.service;

import com.lucasdonato.pixsecurity.customer.dto.CustomerRequest;
import com.lucasdonato.pixsecurity.customer.dto.CustomerResponse;
import com.lucasdonato.pixsecurity.customer.entity.Customer;
import com.lucasdonato.pixsecurity.customer.repository.CustomerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional
    public CustomerResponse create(CustomerRequest request) {
        if (customerRepository.existsByCpf(request.cpf())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "CPF already registered");
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
