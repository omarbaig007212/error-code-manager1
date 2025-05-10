package com.example.error_code_manager.config;

import org.springframework.context.annotation.Bean;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI errorCodeMangerOpenApi() {
        return new OpenAPI()
        .info(new Info().title("Error Code Manager API")
                .description("REST API")
                .version("v1.0.0")
                .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }
}
