package br.com.financial.shared.health;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class HealthControllerTest {

  private final DataSource dataSource = mock(DataSource.class);
  private final HealthController controller = new HealthController(dataSource);

  @Test
  void returnsUpWhenDatabaseConnectionIsValid() throws SQLException {
    Connection connection = mock(Connection.class);
    when(connection.isValid(anyInt())).thenReturn(true);
    when(dataSource.getConnection()).thenReturn(connection);

    ResponseEntity<HealthResponse> response = controller.health();

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().status()).isEqualTo(HealthStatus.UP);
    assertThat(response.getBody().database()).isEqualTo(HealthStatus.UP);
    assertThat(response.getBody().timestamp()).isNotNull();
  }

  @Test
  void returnsServiceUnavailableWhenConnectionIsInvalid() throws SQLException {
    Connection connection = mock(Connection.class);
    when(connection.isValid(anyInt())).thenReturn(false);
    when(dataSource.getConnection()).thenReturn(connection);

    ResponseEntity<HealthResponse> response = controller.health();

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
    assertThat(response.getBody().database()).isEqualTo(HealthStatus.DOWN);
  }

  @Test
  void returnsServiceUnavailableWhenDatabaseIsUnreachable() throws SQLException {
    when(dataSource.getConnection()).thenThrow(new SQLException("connection refused"));

    ResponseEntity<HealthResponse> response = controller.health();

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
    assertThat(response.getBody().status()).isEqualTo(HealthStatus.DOWN);
    assertThat(response.getBody().database()).isEqualTo(HealthStatus.DOWN);
  }
}
