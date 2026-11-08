package br.com.financial;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Starts the full application against a real PostgreSQL container.
 *
 * <p>Requires Docker. The test is skipped (not failed) when Docker is unavailable; CI always
 * provides Docker, so it always runs there.
 */
@Import(TestcontainersConfiguration.class)
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers(disabledWithoutDocker = true)
class BackendApplicationTests {

  @Autowired private MockMvc mockMvc;
  @Autowired private Flyway flyway;
  @Autowired private JdbcTemplate jdbcTemplate;

  @Test
  void contextLoads() {}

  @Test
  void flywayAppliesMigrationsToCleanDatabase() {
    flyway.validate();

    var appliedMigrations = flyway.info().applied();
    assertThat(appliedMigrations).hasSize(1);
    assertThat(appliedMigrations[0].getScript()).isEqualTo("V1__Create_initial_tables.sql");
    assertThat(
            jdbcTemplate.queryForList(
                "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public'",
                String.class))
        .contains("users", "accounts", "flyway_schema_history");
  }

  @Test
  void apiHealthReportsDatabaseUp() throws Exception {
    mockMvc
        .perform(get("/api/v1/health"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("UP"))
        .andExpect(jsonPath("$.database").value("UP"));
  }

  @Test
  void actuatorHealthIsUp() throws Exception {
    mockMvc
        .perform(get("/actuator/health"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("UP"));
  }

  @Test
  void openApiSpecificationIsPubliclyAvailable() throws Exception {
    mockMvc
        .perform(get("/v3/api-docs"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.info.title").value("Financial System API"));
  }

  @Test
  @WithMockUser
  void accountListUsesPaginationEnvelopeAndRequestedSorting() throws Exception {
    mockMvc
        .perform(get("/api/v1/accounts?page=1&size=5&sort=accountNumber,asc"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.page").value(1))
        .andExpect(jsonPath("$.size").value(5))
        .andExpect(jsonPath("$.totalElements").value(0))
        .andExpect(jsonPath("$.totalPages").value(0));
  }

  @Test
  @WithMockUser
  void accountListRejectsUnsupportedSortProperties() throws Exception {
    mockMvc
        .perform(get("/api/v1/accounts?sort=password,asc"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Unsupported sort property: password"));
  }

  @Test
  @WithMockUser
  void accountListCapsPageSizeAtOneHundred() throws Exception {
    mockMvc
        .perform(get("/api/v1/accounts?size=500"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size").value(100));
  }
}
