# Development Setup

How to build and run the Financial Management System locally.

## 1. Prerequisites

Install:

| Tool | Version | Notes |
| --- | --- | --- |
| Git | any recent | |
| Docker Desktop (Windows/macOS) or Docker Engine + Compose v2 | recent | Runs PostgreSQL and the backend integration tests |
| JDK | **25 (LTS)** | e.g. Eclipse Temurin 25. Set `JAVA_HOME`. Maven is provided by `mvnw` |
| Node.js | **24 LTS** | See `frontend/.nvmrc`. npm is bundled |

Optional: IntelliJ IDEA or another Java IDE, Visual Studio Code, a
PostgreSQL client.

Verify:

```bash
git --version
docker compose version
java -version     # must report 25.x
node --version    # must report v24.x
```

## 2. Clone the repository

```bash
git clone <repository-url>
cd Financial-System
```

## 3. Environment configuration

Copy the example environment files:

```bash
cp .env.example .env
cp frontend/.env.example frontend/.env.local
```

On Windows PowerShell:

```powershell
Copy-Item .env.example .env
Copy-Item frontend\.env.example frontend\.env.local
```

Edit `.env` and set local-only values. `POSTGRES_PASSWORD` and
`DB_PASSWORD` must match. Do not use production credentials.

| Variable | Used by | Purpose |
| --- | --- | --- |
| `POSTGRES_DB`, `POSTGRES_USER`, `POSTGRES_PASSWORD` | Docker Compose | Database created on first start |
| `POSTGRES_PORT` | Docker Compose | Host port (bound to `127.0.0.1` only) |
| `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` | Backend | JDBC connection |
| `SERVER_PORT` | Backend | HTTP port (default `8080`) |
| `APP_CORS_ALLOWED_ORIGINS` | Backend | Browser origins allowed to call `/api/**` |
| `NEXT_PUBLIC_API_URL` | Frontend (`frontend/.env.local`) | Backend API base URL |

How configuration is loaded:

- **Docker Compose** reads the root `.env` automatically.
- **Backend** imports the root `.env` (`spring.config.import`) when started
  from `backend/` or the repository root. Values must be unquoted
  `KEY=value` lines. Real environment variables take precedence.
- **Frontend** (Next.js) only reads env files inside `frontend/`, hence
  `frontend/.env.local`.

`.env`, `.env.*` (except `.env.example`) and `frontend/.env.local` are
git-ignored. Never commit them.

## 4. Start PostgreSQL

```bash
docker compose up -d --wait postgres
docker compose ps        # STATUS should show "(healthy)"
```

- Image: `postgres:17-alpine`, database time zone `America/Sao_Paulo`.
- Data is stored in the named volume `financial-system-postgres-data` and
  survives `docker compose down` / restarts.
- Health check: `pg_isready` every 10 s.
- Scripts in `infrastructure/database/init/` run only when the volume is first
  created.

Useful commands:

```bash
docker compose logs -f postgres                              # logs
docker compose exec postgres psql -U finance_user -d finance_db   # SQL shell
docker compose stop                                          # stop, keep data
docker compose down -v                                       # DELETE all local data
```

## 5. Run the backend

Linux/macOS:

```bash
cd backend
./mvnw spring-boot:run
```

Windows PowerShell:

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

The first run downloads Maven and all dependencies. Verify:

```bash
curl http://localhost:8080/api/v1/health
# {"status":"UP","database":"UP","timestamp":"..."}
curl http://localhost:8080/actuator/health
# {"status":"UP","groups":["liveness","readiness"]}
```

`/api/v1/health` returns HTTP `503` with `"database":"DOWN"` when PostgreSQL
is unreachable. Flyway migrations are introduced in Phase 2.

## 6. Run the frontend

In another terminal:

```bash
cd frontend
npm ci
npm run dev
```

Open http://localhost:3000. The home page shows the backend and database
status by calling `GET /api/v1/health`.

| Service | URL |
| --- | --- |
| Frontend | http://localhost:3000 |
| Backend | http://localhost:8080 |
| API base path | `/api/v1` |
| Swagger UI | Phase 2 (Springdoc) |

## 7. Build and test

Backend (from `backend/`):

```bash
./mvnw verify          # Windows: .\mvnw.cmd verify
```

Runs unit tests and Testcontainers integration tests, then packages
`target/backend-0.0.1-SNAPSHOT.jar`. Integration tests start their own
PostgreSQL container and are **skipped** when Docker is not running.

Frontend (from `frontend/`):

```bash
npm ci
npm run lint
npm run typecheck
npm test               # Vitest unit tests
npm run build          # production build
```

## 8. Continuous integration

`.github/workflows/ci.yml` runs on every push to `main` and every pull
request:

| Job | What it checks |
| --- | --- |
| Secret scan | gitleaks scans the full git history for committed secrets |
| Backend | JDK 25, `./mvnw verify` (including Testcontainers tests) |
| Frontend | Node 24, `npm ci`, lint, typecheck, tests, build |
| Docker Compose | PostgreSQL starts from `.env.example` and becomes healthy |

## 9. Database migrations

From Phase 2, create a new Flyway migration for every schema change in
`backend/src/main/resources/db/migration`. Example naming:

```text
V1__create_company_and_users.sql
V2__create_customers.sql
V3__create_products.sql
```

Do not modify a migration that has already been applied in a shared or
production database. Add a new migration instead.

## 10. Local integrations

Email and WhatsApp credentials are optional for initial local
development. Use provider sandbox/test credentials when available.

When credentials are absent:

- The application should clearly indicate that the integration is not configured.
- Tests should use mock adapters.
- Do not report a notification as delivered when it was only simulated.
- Never send messages to real customers during automated tests.

## 11. Troubleshooting

### Database connection failure

- `docker compose ps` — is `postgres` running and `(healthy)`?
- Is port 5432 already used by another PostgreSQL? Change `POSTGRES_PORT`
  and the port in `DB_URL`.
- `DB_PASSWORD` must equal `POSTGRES_PASSWORD`. Changing
  `POSTGRES_PASSWORD` after the volume exists has **no effect**; either keep
  the original password or reset with `docker compose down -v`.
- Backend fails with `Could not resolve placeholder 'DB_PASSWORD'`: the root
  `.env` is missing or the backend was started from another directory.

### `JAVA_HOME` / wrong Java version

`mvnw` uses `JAVA_HOME` (or `java` on `PATH`). It must point to a JDK 25.

### Backend integration tests are skipped

Docker is not running. Start Docker Desktop and re-run `./mvnw verify`.

### Turbopack: "node process exited before we could connect to it"

Turbopack runs CSS/Tailwind processing in helper Node processes. This error
usually means a stale or unwritable `frontend/.next` directory (for example,
left behind by an interrupted build or created by another user/tool). Stop
`npm run dev`, delete `frontend/.next` and retry. If it persists, check that
antivirus/endpoint security is not blocking local Node connections.

### Migration failure

- Inspect Flyway logs.
- Verify migration naming and SQL syntax.
- Do not manually change Flyway history to hide a failed migration.

### CORS failure

- Confirm the frontend origin is listed in `APP_CORS_ALLOWED_ORIGINS`.
- Avoid wildcard origins with credentials enabled.

### WhatsApp or email failure

- Verify provider credentials and configuration.
- Check template approval and recipient consent where applicable.
- Inspect provider response IDs and error details without logging
  secrets.

## 12. Production readiness

Before deployment:

- Configure HTTPS.
- Move secrets to a secure secret manager.
- Configure database backups and test restoration.
- Restrict database network access.
- Configure monitoring and log retention.
- Review file upload security.
- Configure notification retry limits.
- Test access control and financial workflows.
- Document rollback and incident procedures.
