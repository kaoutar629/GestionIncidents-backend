package com.kaoutar.gestionIncidents.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * ✅ Configuration Swagger / OpenAPI
 *
 *  Accès :
 *    - Swagger UI  : http://localhost:8080/swagger-ui.html
 *    - JSON OpenAPI: http://localhost:8080/v3/api-docs
 *
 *  Le schéma "bearerAuth" permet de tester les endpoints protégés
 *  directement depuis l'interface Swagger avec le token JWT.
 */
@Configuration
@EnableAsync
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Gestion Incidents API")
                        .version("1.0")
                        .description("API REST pour la gestion des incidents — JWT Authentication"))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Collez ici le token reçu depuis /api/auth/login")
                        )
                );
    }
}

