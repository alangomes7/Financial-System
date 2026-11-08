package br.com.financial.shared.config;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.com.financial.BackendApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;

class MissingConfigurationTest {

  @Test
  void applicationFailsClearlyWhenRequiredDatabasePasswordIsMissing() {
    assertThatThrownBy(
            () ->
                new SpringApplicationBuilder(BackendApplication.class)
                    .run(
                        "--spring.config.location=classpath:/application-missing-config.yml",
                        "--spring.config.import="))
        .isInstanceOf(Exception.class)
        .hasStackTraceContaining("Could not resolve placeholder 'MISSING_REQUIRED_DB_PASSWORD'");
  }
}
