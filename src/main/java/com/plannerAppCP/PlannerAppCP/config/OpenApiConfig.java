package com.plannerAppCP.PlannerAppCP.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI plannerAppOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("PlannerAppCP API")
                        .version("1.0")
                        .description("API REST para la gestión de tareas personales. Permite crear, consultar, actualizar y eliminar tareas con prioridad, categoría y estado.")
                        .contact(new Contact()
                                .name("Carol")
                                .email("carol@ejemplo.com")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor de desarrollo")));
    }
}
