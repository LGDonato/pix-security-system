package com.lucasdonato.pixsecurity.customer.repository;

import com.lucasdonato.pixsecurity.customer.entity.Customer;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

// Camada de acesso ao banco. JpaRepository fornece operacoes CRUD para Customer.
public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    // Query derivada pelo Spring Data para checar duplicidade de CPF.
    boolean existsByCpf(String cpf);
}
