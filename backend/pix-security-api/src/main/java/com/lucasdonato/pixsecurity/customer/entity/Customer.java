package com.lucasdonato.pixsecurity.customer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

// Entidade JPA gerenciada pelo Hibernate e persistida na tabela pix_customers.
@Entity
@Table(name = "pix_customers")

public class Customer {

    // Chave primaria da entidade. O UUID e armazenado como CHAR(36) para compatibilidade com MySQL.
    @Id
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "id", columnDefinition = "CHAR(36)")
    private UUID id;

    // CPF obrigatorio e unico para impedir cadastro duplicado.
    @Column(nullable = false, unique = true, length = 11)
    private String cpf;

    // Nome completo obrigatorio na validacao e no banco.
    @NotBlank
    @Column(name = "full_name", nullable = false)
    private String fullName;

    // Campos opcionais: podem ser persistidos como nulos.
    @Column
    private String email;

    @Column
    private String phone;

    // Enum persistido como texto para manter o banco legivel e evitar dependencia da ordem dos valores.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    // Campos de auditoria preenchidos automaticamente nos callbacks JPA.
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public enum Status {
        ACTIVE,
        INACTIVE
    }

    // Construtor exigido pelo JPA para materializar entidades vindas do banco.
    protected Customer() {
    }

    public Customer(String cpf, String fullName, String email, String phone) {
        this.cpf = cpf;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        // Regra de negocio: todo cliente novo nasce ativo.
        this.status = Status.ACTIVE;
    }

    // Callback JPA executado antes do INSERT para preencher id, datas e status default.
    @PrePersist
    void prePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }

        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;

        if (status == null) {
            status = Status.ACTIVE;
        }
    }

    // Callback JPA executado antes do UPDATE para renovar a data de alteracao.
    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public String getCpf() {
        return cpf;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public Status getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
