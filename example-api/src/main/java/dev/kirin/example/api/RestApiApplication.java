package dev.kirin.example.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import dev.kirin.example.api.constants.Constants;

@SpringBootApplication
@ComponentScan(basePackages = {Constants.BASE_PACKAGE})
public class RestApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestApiApplication.class, args);
	}

}
