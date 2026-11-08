package br.com.financial.shared.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI financialSystemOpenApi() {
    return new OpenAPI()
        .info(
            new Info()
                .title("Financial System API")
                .version("v1")
                .description("REST API for the Financial Management System."));
  }
}
