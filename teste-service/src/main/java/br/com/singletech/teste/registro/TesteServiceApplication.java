package br.com.singletech.teste.registro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class TesteServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TesteServiceApplication.class, args);
	}

}
