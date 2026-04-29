// Define o pacote da camada repository do módulo customer.
package com.lucasdonato.pixsecurity.customer.repository;

// Importa a entidade Customer que será persistida e consultada.
import com.lucasdonato.pixsecurity.customer.entity.Customer;
// UUID é o tipo da chave primária da entidade Customer.
import java.util.UUID;
// JpaRepository fornece métodos prontos de CRUD, como save, findById, findAll e delete.
import org.springframework.data.jpa.repository.JpaRepository;

// Repository é a camada de acesso ao banco de dados usando Spring Data JPA.
public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    // Spring Data cria automaticamente uma consulta para verificar se existe registro com este CPF.
    boolean existsByCpf(String cpf);
}
