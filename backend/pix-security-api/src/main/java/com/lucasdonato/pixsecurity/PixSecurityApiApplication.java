// Define o pacote onde esta classe principal da aplicação está localizada.
package com.lucasdonato.pixsecurity;

// Importa a classe responsável por iniciar uma aplicação Spring Boot.
import org.springframework.boot.SpringApplication;
// Importa a anotação principal do Spring Boot.
import org.springframework.boot.autoconfigure.SpringBootApplication;

// @SpringBootApplication ativa auto configuração, configuração Spring e busca automática por componentes.
// scanBasePackages manda o Spring procurar controllers, services e repositories dentro de com.lucasdonato.
@SpringBootApplication(scanBasePackages = "com.lucasdonato")
// Classe principal do projeto.
public class PixSecurityApiApplication {

	// Método main: ponto de entrada de qualquer aplicação Java executável.
	public static void main(String[] args) {
		// Sobe o contexto do Spring, configura os beans e inicia o servidor web embutido.
		SpringApplication.run(PixSecurityApiApplication.class, args);
	}

}
