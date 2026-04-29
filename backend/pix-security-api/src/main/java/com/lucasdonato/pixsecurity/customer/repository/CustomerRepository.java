package com.lucasdonato.pixsecurity.customer.repository;

import com.lucasdonato.pixsecurity.customer.entity.Customer;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    boolean existsByCpf(String cpf);
}
