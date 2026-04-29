// Define o pacote onde a classe de teste está localizada.
package com.lucasdonato.pix_security;

// @Test marca um método como teste executável pelo JUnit.
import org.junit.jupiter.api.Test;
// @SpringBootTest sobe o contexto completo do Spring Boot para testar a inicialização.
import org.springframework.boot.test.context.SpringBootTest;

// @SpringBootTest verifica se a aplicação consegue carregar seus beans e configurações.
@SpringBootTest
// Classe de teste principal da aplicação.
class PixSecurityApiApplicationTests {

	// @Test indica que este método será executado como teste automatizado.
	@Test
	// Teste vazio de propósito: se o contexto Spring carregar sem erro, o teste passa.
	void contextLoads() {
	}

}
