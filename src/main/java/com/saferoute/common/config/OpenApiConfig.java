package com.saferoute.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SafeRoute API")
                        .version("1.0.0")
                        .description("""
                                ## Sistema de Rutas Escolares - SafeRoute
                                
                                API RESTful para la gestión de rutas escolares, estudiantes, conductores y notificaciones.
                                
                                ### Roles de Usuario
                                - **ADMIN**: Acceso total a todos los recursos
                                - **DRIVER**: Gestión de rutas, GPS y eventos
                                - **GUARDIAN**: Consulta de estudiantes y notificaciones
                                
                                ### Autenticación
                                Todos los endpoints (excepto `/auth/**`) requieren JWT Bearer Token.
                                """)
                        .contact(new Contact()
                                .name("SafeRoute Team")
                                .email("dev@saferoute.com")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT Bearer token authentication")));
    }
}
