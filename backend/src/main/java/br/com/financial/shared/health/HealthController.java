package br.com.financial.shared.health;

import java.sql.Connection;
import java.time.Instant;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Lightweight application health endpoint exposed under the versioned API path.
 *
 * <p>Returns {@code 200 OK} when the application can reach PostgreSQL and {@code 503 Service
 * Unavailable} otherwise. Detailed operational health is also available through Spring Boot
 * Actuator at {@code /actuator/health}.
 */
@RestController
@RequestMapping("/api/v1/health")
public class HealthController {

  private static final Logger log = LoggerFactory.getLogger(HealthController.class);
  private static final int VALIDATION_TIMEOUT_SECONDS = 2;

  private final DataSource dataSource;

  public HealthController(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @GetMapping
  public ResponseEntity<HealthResponse> health() {
    HealthStatus database = checkDatabase();
    HealthStatus overall = database == HealthStatus.UP ? HealthStatus.UP : HealthStatus.DOWN;
    HttpStatus httpStatus =
        overall == HealthStatus.UP ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE;
    return ResponseEntity.status(httpStatus)
        .body(new HealthResponse(overall, database, Instant.now()));
  }

  private HealthStatus checkDatabase() {
    try (Connection connection = dataSource.getConnection()) {
      return connection.isValid(VALIDATION_TIMEOUT_SECONDS) ? HealthStatus.UP : HealthStatus.DOWN;
    } catch (Exception ex) {
      // Do not log connection details: they may contain credentials.
      log.warn("Database health check failed: {}", ex.getClass().getSimpleName());
      return HealthStatus.DOWN;
    }
  }
}
