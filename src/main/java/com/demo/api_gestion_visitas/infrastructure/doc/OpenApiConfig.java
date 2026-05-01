package com.demo.api_gestion_visitas.infrastructure.doc;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI apiGestionVisitasOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API-GESTION-VISITAS DOCUMENTACION")
                        .version("1.0"));
    }
}
