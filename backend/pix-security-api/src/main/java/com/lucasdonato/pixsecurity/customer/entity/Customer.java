// Define o pacote da entidade Customer dentro do módulo de clientes.
package com.lucasdonato.pixsecurity.customer.entity;

// @Column configura como um atributo Java será mapeado para uma coluna do banco.
import jakarta.persistence.Column;
// @Entity informa ao JPA/Hibernate que esta classe representa uma tabela.
import jakarta.persistence.Entity;
// EnumType define a forma de salvar enums no banco, por exemplo como texto.
import jakarta.persistence.EnumType;
// @Enumerated configura o mapeamento de atributos enum.
import jakarta.persistence.Enumerated;
// @GeneratedValue é usado quando o banco ou o JPA geram o valor da chave automaticamente.
import jakarta.persistence.GeneratedValue;
// @Id marca o atributo que será a chave primária da tabela.
import jakarta.persistence.Id;
// @PrePersist executa um método automaticamente antes de inserir a entidade no banco.
import jakarta.persistence.PrePersist;
// @PreUpdate executa um método automaticamente antes de atualizar a entidade no banco.
import jakarta.persistence.PreUpdate;
// @Table permite informar o nome da tabela relacionada à entidade.
import jakarta.persistence.Table;
// @NotBlank valida que uma String não seja nula, vazia ou composta só por espaços.
import jakarta.validation.constraints.NotBlank;
// LocalDateTime representa data e hora sem fuso horário.
import java.time.LocalDateTime;
// UUID representa um identificador único universal.
import java.util.UUID;
// @JdbcTypeCode permite escolher o tipo JDBC usado pelo Hibernate para persistir um campo.
import org.hibernate.annotations.JdbcTypeCode;
// SqlTypes contém constantes de tipos SQL usadas pelo Hibernate.
import org.hibernate.type.SqlTypes;

// @Entity faz o Hibernate gerenciar esta classe como uma entidade persistente.
@Entity
// @Table define que esta entidade será armazenada na tabela pix_customers.
@Table(name = "pix_customers")
// Classe que representa um cliente tanto no código Java quanto no banco de dados.
public class Customer {

    // @Id indica que este campo é a chave primária da entidade.
    @Id
    // Define que o UUID será tratado como CHAR pelo Hibernate.
    @JdbcTypeCode(SqlTypes.CHAR)
    // Mapeia o campo id para a coluna id com tipo CHAR(36), formato textual comum de UUID.
    @Column(name = "id", columnDefinition = "CHAR(36)")
    // Identificador único do cliente.
    private UUID id;

    // Mapeia cpf como coluna obrigatória, única e com tamanho máximo de 11 caracteres.
    @Column(nullable = false, unique = true, length = 11)
    // CPF do cliente, usado para identificar duplicidade.
    private String cpf;

    // Valida que o nome completo não pode estar em branco.
    @NotBlank
    // Mapeia fullName para a coluna full_name e obriga preenchimento no banco.
    @Column(name = "full_name", nullable = false)
    // Nome completo do cliente.
    private String fullName;

    // Mapeia email como coluna no banco; como nullable não é false, é opcional.
    @Column
    // E-mail opcional do cliente.
    private String email;

    // Mapeia phone como coluna no banco; como nullable não é false, é opcional.
    @Column
    // Telefone opcional do cliente.
    private String phone;

    // Salva o enum como texto no banco, por exemplo ACTIVE ou INACTIVE.
    @Enumerated(EnumType.STRING)
    // Mapeia status como coluna obrigatória.
    @Column(nullable = false)
    // Status atual do cliente.
    private Status status;

    // Mapeia createdAt para created_at, obrigatório e sem atualização após o primeiro INSERT.
    @Column(name = "created_at", nullable = false, updatable = false)
    // Data e hora em que o cliente foi criado.
    private LocalDateTime createdAt;

    // Mapeia updatedAt para updated_at e exige valor no banco.
    @Column(name = "updated_at", nullable = false)
    // Data e hora da última atualização do cliente.
    private LocalDateTime updatedAt;

    // Enum que limita os status possíveis do cliente.
    public enum Status {
        // Indica que o cliente está ativo.
        ACTIVE,
        // Indica que o cliente está inativo.
        INACTIVE
    }

    // Construtor protegido exigido pelo JPA para reconstruir objetos vindos do banco.
    protected Customer() {
    }

    // Construtor usado pela aplicação para criar um novo cliente.
    public Customer(String cpf, String fullName, String email, String phone) {
        // Armazena o CPF recebido no atributo da entidade.
        this.cpf = cpf;
        // Armazena o nome completo recebido no atributo da entidade.
        this.fullName = fullName;
        // Armazena o e-mail recebido no atributo da entidade.
        this.email = email;
        // Armazena o telefone recebido no atributo da entidade.
        this.phone = phone;
        // Define ACTIVE como status padrão ao criar um cliente.
        this.status = Status.ACTIVE;
    }

    // @PrePersist faz este método rodar antes de salvar um novo registro no banco.
    @PrePersist
    // Prepara campos automáticos antes da primeira persistência.
    void prePersist() {
        // Verifica se o id ainda não foi preenchido.
        if (id == null) {
            // Gera manualmente um UUID novo para identificar o cliente.
            id = UUID.randomUUID();
        }

        // Captura a data e hora atual para preencher os campos de auditoria.
        LocalDateTime now = LocalDateTime.now();
        // Define o momento de criação do registro.
        createdAt = now;
        // Define o momento inicial de atualização do registro.
        updatedAt = now;

        // Garante que status nunca seja salvo como nulo.
        if (status == null) {
            // Usa ACTIVE como padrão quando nenhum status foi definido.
            status = Status.ACTIVE;
        }
    }
    // @PreUpdate faz este método rodar antes de atualizar um registro existente.
    @PreUpdate
    // Atualiza campos automáticos antes de um UPDATE.
    void preUpdate() {
        // Marca a data e hora da última atualização.
        updatedAt = LocalDateTime.now();
    }

    // Getter para acessar o id do cliente.
    public UUID getId() {
        // Retorna o identificador único do cliente.
        return id;
    }

    // Getter para acessar o CPF do cliente.
    public String getCpf() {
        // Retorna o CPF armazenado.
        return cpf;
    }

    // Getter para acessar o nome completo do cliente.
    public String getFullName() {
        // Retorna o nome completo armazenado.
        return fullName;
    }

    // Getter para acessar o e-mail do cliente.
    public String getEmail() {
        // Retorna o e-mail armazenado.
        return email;
    }

    // Getter para acessar o telefone do cliente.
    public String getPhone() {
        // Retorna o telefone armazenado.
        return phone;
    }

    // Getter para acessar o status do cliente.
    public Status getStatus() {
        // Retorna o status atual.
        return status;
    }

    // Getter para acessar a data de criação.
    public LocalDateTime getCreatedAt() {
        // Retorna quando o cliente foi criado.
        return createdAt;
    }

    // Getter para acessar a data da última atualização.
    public LocalDateTime getUpdatedAt() {
        // Retorna quando o cliente foi atualizado pela última vez.
        return updatedAt;
    }
}
