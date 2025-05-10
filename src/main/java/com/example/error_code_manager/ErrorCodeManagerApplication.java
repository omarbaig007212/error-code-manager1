package com.example.error_code_manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Product Version Error Code APIs", version = "2.0",description = "API's Docs"))
public class ErrorCodeManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ErrorCodeManagerApplication.class, args);
	}

}
