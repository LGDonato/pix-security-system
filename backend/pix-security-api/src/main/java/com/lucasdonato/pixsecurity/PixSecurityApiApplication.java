package com.lucasdonato.pixsecurity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.lucasdonato")
public class PixSecurityApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(PixSecurityApiApplication.class, args);
	}

}
