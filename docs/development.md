# Development Setup

This document describes the intended local development workflow.
Commands may need minor adjustments after the actual repository files
and build configuration are created.

## 1. Prerequisites

Install: - Git. - Docker Desktop (Windows) or Docker Engine and
Compose. - Java 21 or the selected compatible LTS release. - Node.js
LTS. - npm.

Optional: - IntelliJ IDEA or another Java IDE. - Visual Studio Code. - A
PostgreSQL client.

## 2. Clone the repository

``` bash
git clone <repository-url>
cd financial-management-system
```

## 3. Environment configuration

Copy the example environment file:

``` bash
cp .env.example .env
```

On Windows PowerShell:

``` powershell
Copy-Item .env.example .env
```

Set local development values. Do not use production credentials.

Example variables (the final names must match the implemented Compose
and Spring configuration):

``` env
POSTGRES_DB=finance_db
POSTGRES_USER=finance_user
POSTGRES_PASSWORD=change_me

DB_URL=jdbc:postgresql://localhost:5432/finance_db
DB_USERNAME=finance_user
DB_PASSWORD=change_me

JWT_SECRET=replace_with_a_long_random_development_secret

EMAIL_HOST=
EMAIL_PORT=
EMAIL_USERNAME=
EMAIL_PASSWORD=

WHATSAPP_API_URL=
WHATSAPP_ACCESS_TOKEN=
WHATSAPP_PHONE_NUMBER_ID=

NEXT_PUBLIC_API_URL=http://localhost:8080/api/v1
```

Never commit `.env`. Keep `.env.example` free of real secrets.

## 4. Start PostgreSQL

After `docker-compose.yml` is implemented:

``` bash
docker compose up -d postgres
docker compose ps
```

Check the container health and logs before starting the backend.

## 5. Run the backend

Linux/macOS:

``` bash
cd backend
./mvnw spring-boot:run
```

Windows PowerShell:

``` powershell
cd backend
.\mvnw.cmd spring-boot:run
```

Flyway should apply pending migrations using the configured datasource.
Confirm the application logs show successful startup and migration
completion.

## 6. Run the frontend

In another terminal:

``` bash
cd frontend
npm install
npm run dev
```

Expected local address: - Frontend: http://localhost:3000 - Backend:
http://localhost:8080 - API base path: `/api/v1` - Swagger UI: commonly
http://localhost:8080/swagger-ui/index.html

The exact Swagger URL depends on the Springdoc configuration.

## 7. Tests

Backend:

``` bash
cd backend
./mvnw test
```

Windows:

``` powershell
cd backend
.\mvnw.cmd test
```

Frontend:

``` bash
cd frontend
npm test
```

The frontend test command must be configured when selecting the test
framework. Also provide lint and type-check scripts, for example:

``` bash
npm run lint
npm run typecheck
```

## 8. Database migrations

Create a new Flyway migration for every schema change. Example naming:

``` text
V1__create_company_and_users.sql
V2__create_customers.sql
V3__create_products.sql
```

Do not modify a migration that has already been applied in a shared or
production database. Add a new migration instead.

## 9. Local integrations

Email and WhatsApp credentials are optional for initial local
development. Use provider sandbox/test credentials when available.

When credentials are absent: - The application should clearly indicate
that the integration is not configured. - Tests should use mock
adapters. - Do not report a notification as delivered when it was only
simulated. - Never send messages to real customers during automated
tests.

## 10. Troubleshooting

### Database connection failure

-   Confirm PostgreSQL container is running.
-   Check port mapping.
-   Verify database name, username and password.
-   Confirm the backend uses the correct JDBC URL.

### Migration failure

-   Inspect Flyway logs.
-   Verify migration naming and SQL syntax.
-   Do not manually change Flyway history to hide a failed migration.

### CORS failure

-   Confirm the frontend origin is explicitly allowed in Spring
    Security.
-   Avoid wildcard origins with credentials enabled.

### WhatsApp or email failure

-   Verify provider credentials and configuration.
-   Check template approval and recipient consent where applicable.
-   Inspect provider response IDs and error details without logging
    secrets.

## 11. Production readiness

Before deployment: - Configure HTTPS. - Move secrets to a secure secret
manager. - Configure database backups and test restoration. - Restrict
database network access. - Configure monitoring and log retention. -
Review file upload security. - Configure notification retry limits. -
Test access control and financial workflows. - Document rollback and
incident procedures.
