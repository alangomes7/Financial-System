# Architecture

## 1. Architectural approach

Use a **modular monolith**: one deployable Spring Boot backend
containing clearly separated business modules, plus a separate Next.js
frontend. This is simpler to build, deploy and operate than
microservices for the initial single-company scope.

Modules should communicate through explicit service interfaces and
domain-level operations. Avoid direct access to another module's
repositories where a service API is appropriate.

## 2. Logical architecture

``` text
Browser / Mobile browser
          |
          v
Next.js frontend (TypeScript)
          |
          | HTTPS / JSON REST
          v
Spring Boot application
  ├── Authentication and authorization
  ├── Company settings
  ├── Customers and CRM
  ├── Products and inventory
  ├── Sales and payments
  ├── Finance (receivables, payables, expenses)
  ├── Negotiations
  ├── Payroll (internal statements)
  ├── Notifications
  ├── Reports
  └── Audit
          |
          v
PostgreSQL database
          |
          +---- File storage abstraction
          |
          +---- Background job / notification processing
                         |
                         +---- Email provider
                         +---- WhatsApp Business Cloud API
```

## 3. Technology choices

### Frontend

-   Next.js and React with TypeScript.
-   Tailwind CSS and shadcn/ui.
-   React Hook Form and Zod for form state and client-side validation.
-   Recharts for charts.
-   A centralized typed API client; do not scatter raw `fetch` calls
    throughout UI components.

### Backend

-   Java 25 (LTS) for the backend runtime and build.
-   Spring Boot.
-   Spring Security.
-   Spring Data JPA and Hibernate.
-   Bean Validation.
-   Springdoc OpenAPI.
-   BigDecimal for all monetary calculations.

### Persistence

-   PostgreSQL as the authoritative transactional database.
-   Flyway for version-controlled schema migrations.
-   NUMERIC/DECIMAL columns for monetary values.
-   Foreign keys, unique constraints, indexes and transactions for
    integrity.

### Infrastructure

-   Docker and Docker Compose for local development.
-   GitHub Actions for build and test automation.
-   Environment-based configuration.
-   Automated database backups and a tested restore procedure before
    production use.

Pin compatible dependency versions when initializing the repository; do
not assume that every library's latest release is mutually compatible.

## 4. Single-company model

The initial application serves one company. Store company identity and
configuration in a `company` record, and associate company-owned
business data with a company identifier where useful for consistency and
future migration.

Do not implement tenant routing, tenant resolution or cross-tenant
isolation in the MVP. Do not expose a user-selectable company identifier
as an authorization mechanism.

If multi-tenancy is added later, it requires a separate security and
data-isolation design review. Adding a `company_id` column alone does
not make an application safely multi-tenant.

## 5. Backend module boundaries

Suggested packages:

``` text
com.financesystem
├── config
├── security
├── authentication
├── company
├── users
├── customers
├── products
├── inventory
├── sales
├── finance
│   ├── receivables
│   ├── payables
│   ├── payments
│   └── expenses
├── payroll
├── negotiations
├── notifications
├── reports
├── audit
└── common
```

Each module may contain: - `controller`: HTTP endpoints and
request/response mapping. - `service`: business use cases and
transaction boundaries. - `repository`: persistence interfaces. -
`entity`: persistence model. - `dto`: request and response objects. -
`mapper`: entity/DTO mapping. - `validation`: module-specific
validation.

Controllers must not contain business logic. Do not return JPA entities
directly from public APIs.

## 6. Frontend module boundaries

``` text
frontend/
└── src/
    ├── app/
    ├── components/
    ├── features/
    │   ├── dashboard/
    │   ├── customers/
    │   ├── products/
    │   ├── sales/
    │   ├── finance/
    │   ├── payroll/
    │   ├── negotiations/
    │   ├── notifications/
    │   ├── reports/
    │   └── settings/
    ├── hooks/
    ├── lib/
    ├── services/
    ├── schemas/
    └── types/
```

Keep presentation components reusable and feature-specific logic within
its feature. Enforce authorization on the backend even when the frontend
hides unavailable navigation items.

## 7. Financial transaction boundaries

Financial operations must be atomic. For example, converting a sale into
receivables should either create all required records or create none.

Use database transactions for: - Creating a sale and its line items. -
Creating receivables from sale payment terms. - Registering a payment
and allocating it to one or more receivables. - Cancelling or reversing
financial records. - Converting an approved negotiation into a sale.

Use idempotency keys or equivalent duplicate-operation protection for
payment registration and external callbacks.

Never derive the authoritative balance from a client-submitted total.
Recalculate on the backend using persisted records.

## 8. Background processing

Notifications and scheduled work must not block HTTP requests.

Use a persistent job/queue approach appropriate to the deployment. For a
small initial installation, a database-backed outbox and scheduled
worker can be sufficient. If a broker is introduced later, keep the
notification service behind an abstraction.

Requirements: - Persist jobs before processing. - Track attempts and
next-attempt time. - Use bounded retries with exponential backoff. -
Make handlers idempotent. - Record provider message IDs and delivery
outcomes. - Provide a way to inspect and retry failed jobs safely.

## 9. File and document storage

Use a storage abstraction for negotiation attachments, receipts and
generated PDFs. Local filesystem storage may be used for development;
production should use durable storage with backups and access controls.

Store metadata in PostgreSQL: - Original filename. - Generated storage
key. - MIME type. - Size. - Uploading user. - Related business record. -
Creation timestamp.

Validate file type and size, generate storage names server-side, and do
not trust client-provided paths.

## 10. Security architecture

-   Spring Security must protect every non-public endpoint.
-   Use password hashing (BCrypt or Argon2).
-   Keep access tokens short-lived.
-   Rotate and revoke refresh tokens.
-   Prefer secure, HttpOnly, SameSite cookies for browser token handling
    where the deployment architecture supports them.
-   Apply CSRF protections when cookie-based authentication is used.
-   Restrict CORS to known frontend origins.
-   Rate-limit login, password recovery and sensitive endpoints.
-   Validate authorization at the service/resource level.
-   Keep secrets out of Git and logs.
-   Audit sensitive financial and administrative operations.

## 11. Future SaaS expansion

Future multi-tenant support is explicitly outside the initial scope.
Before adding it, define: - Tenant identity and resolution. -
Tenant-scoped authorization. - Database isolation strategy. -
Tenant-aware background jobs and file storage. - Tenant-specific backups
and retention. - Cross-tenant security tests. - Subscription and usage
enforcement.

Do not prematurely add microservices or subscription infrastructure to
the single-company MVP.
