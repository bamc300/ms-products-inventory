package com.linktic.msinventory.infrastructure.adapter.config;

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
  public OpenAPI customOpenAPI() {
    return new OpenAPI().info(new Info().title("Inventory Service API").version("1.0.0").description(
        "API de Inventario. Para probar, use el botón **Authorize** e ingrese la API Key."))
        .addSecurityItem(new SecurityRequirement().addList("ApiKeyAuth"))
        .components(new Components().addSecuritySchemes("ApiKeyAuth",
            new SecurityScheme().type(SecurityScheme.Type.APIKEY).in(SecurityScheme.In.HEADER).name("X-API-Key")
                .description("Ingrese la API Key.")));
  }
}
