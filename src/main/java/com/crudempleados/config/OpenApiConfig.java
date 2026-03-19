package com.crudempleados.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI empleadosOpenApi() {
        String schemeName = "basicAuth";
        return new OpenAPI()
            .info(new Info()
                .title("Empleados API")
                .version("1.2.0")
                .description("CRUD administrativo de empleados y login simbolico publico de empleados."))
            .addSecurityItem(new SecurityRequirement().addList(schemeName))
            .components(new Components().addSecuritySchemes(schemeName,
                new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("basic")));
    }
}
