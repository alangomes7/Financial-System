# Backend — Spring Boot

Java 25 (LTS) · Spring Boot 4.1 · Maven (via the included wrapper) · PostgreSQL.

## Requirements

- JDK 25 (e.g. Eclipse Temurin 25) with `JAVA_HOME` set
- Docker, for PostgreSQL and the Testcontainers integration tests
- Maven is **not** required: use `./mvnw` (Linux/macOS) or `.\mvnw.cmd` (Windows)

## Configuration

Settings live in `src/main/resources/application.yml` and are supplied through
environment variables. For local development the backend automatically
imports the repository root `.env` file (`KEY=value` lines; do not quote
values). Real environment variables take precedence.

| Variable                   | Default                                      | Notes                    |
| -------------------------- | -------------------------------------------- | ------------------------ |
| `DB_URL`                   | `jdbc:postgresql://localhost:5432/finance_db` |                          |
| `DB_USERNAME`              | `finance_user`                               |                          |
| `DB_PASSWORD`              | — (required)                                 | Startup fails if missing |
| `SERVER_PORT`              | `8080`                                       |                          |
| `APP_CORS_ALLOWED_ORIGINS` | `http://localhost:3000`                      | Comma-separated          |

## Commands

Run from `backend/` (use `.\mvnw.cmd` on Windows):

| Command                    | Description                                   |
| -------------------------- | --------------------------------------------- |
| `./mvnw spring-boot:run`   | Run against the Compose PostgreSQL            |
| `./mvnw test`              | Unit + integration tests                      |
| `./mvnw verify`            | Tests and build `target/backend-*.jar`        |
| `java -jar target/backend-0.0.1-SNAPSHOT.jar` | Run the packaged jar       |

Integration tests (`BackendApplicationTests`) start a throwaway PostgreSQL
container with Testcontainers. They are **skipped** when Docker is not
available; CI always runs them.

## Endpoints

| Endpoint                        | Description                                       |
| ------------------------------- | ------------------------------------------------- |
| `GET /api/v1/health`            | `200` + `{"status":"UP","database":"UP",...}` or `503` when the database is unreachable |
| `GET /api/v1/accounts`          | Paginated account list; supports `page`, `size`, `sort`, and `accountNumber` |
| `GET /actuator/health`          | Spring Boot Actuator aggregate health (includes DB) |
| `GET /actuator/health/liveness` | Liveness probe                                    |
| `GET /actuator/health/readiness`| Readiness probe                                   |
| `GET /v3/api-docs`              | OpenAPI specification                             |
| `GET /swagger-ui.html`          | Swagger UI                                        |

## Package layout

```text
br.com.financial
├── BackendApplication.java
└── shared/              # Cross-cutting code (config, health, …)
```

Business modules will be added as feature packages (e.g. `customers`,
`sales`) from Phase 2 onwards.
