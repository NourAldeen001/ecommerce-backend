package com.ecommerce.ecommerce_backend.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEMA_NAME = "bearerAuth";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("E-Commerce Backend API")
                        .description("Production-grade e-commerce REST API")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Nour Eldin Ali")
                                .email("noureldinali74@gmail.com")
                        )
                        .license(new License()
                                .name("MIT License")
                        )
                )
                .addSecurityItem(new SecurityRequirement()
                        .addList(SECURITY_SCHEMA_NAME)
                )
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEMA_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEMA_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Paste your JWT token here. Get it POST /api/v1/auth/login")
                        )
                );
    }
}
