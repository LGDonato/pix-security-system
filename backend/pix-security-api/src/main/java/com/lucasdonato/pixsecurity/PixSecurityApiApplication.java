package com.lucasdonato.pixsecurity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Inicializa a aplicacao Spring Boot e habilita o scan dos componentes do projeto.
// O scanBasePackages garante que controllers, services e repositories em com.lucasdonato sejam registrados pelo Spring.
@SpringBootApplication(scanBasePackages = "com.lucasdonato")
public class PixSecurityApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(PixSecurityApiApplication.class, args);
	}

}
