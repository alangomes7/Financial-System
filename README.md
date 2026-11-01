# Financial Management System

A modern, secure and modular financial management application for a
single Brazilian company. It centralizes customers, products and
services, sales, income, expenses, accounts payable and receivable,
internal employee payslips, customer negotiations, payment reminders and
financial reporting.

The initial release is a **single-company application**. The
architecture should remain modular and extensible so that multi-tenant
SaaS capabilities can be introduced in a future project phase without
complicating the MVP.

## Project status

**Stage:** Requirements and architecture specification\
**Current scope:** Single company, Brazilian Portuguese interface, BRL
currency, internal payslips, email and WhatsApp reminders.

> This repository documentation defines the target requirements. It does
> not imply that the application has already been implemented.

## Key decisions

  -----------------------------------------------------------------------
  Area                                Decision
  ----------------------------------- -----------------------------------
  Business model                      Single company

  Architecture                        Modular monolith

  Frontend                            Next.js, React, TypeScript

  Backend                             Java 21+ and Spring Boot

  Database                            PostgreSQL

  Persistence                         Spring Data JPA / Hibernate

  Migrations                          Flyway

  UI                                  Tailwind CSS and shadcn/ui

  Charts                              Recharts

  Validation                          Zod (frontend), Bean Validation
                                      (backend)

  API                                 Versioned REST API

  API documentation                   Springdoc OpenAPI

  Authentication                      Spring Security and JWT

  Infrastructure                      Docker and Docker Compose

  Notifications                       Email and official WhatsApp
                                      Business Platform Cloud API

  Language                            Brazilian Portuguese (`pt-BR`)

  Currency                            Brazilian Real (`BRL`)

  Date format                         `DD/MM/YYYY`

  Time zone                           `America/Sao_Paulo`
  -----------------------------------------------------------------------

See [Architecture](docs/architecture.md),
[Requirements](docs/requirements.md), [Database
Design](docs/database.md), [API Guidelines](docs/api.md), [Development
Roadmap](docs/roadmap.md), and [Development Setup](docs/development.md).

## Main capabilities

-   **Dashboard:** income, expenses, net result, receivables, payables,
    overdue amounts and financial charts.
-   **Customers (CRM):** customer profiles, contact details, purchase
    and payment history.
-   **Products and services:** catalog, pricing, costs, categories and
    optional inventory tracking.
-   **Sales:** sales with multiple items, discounts, payment terms,
    installments and partial payments.
-   **Financial management:** accounts receivable, accounts payable,
    income, expenses and cash-flow reporting.
-   **Negotiations:** proposals, notes, attachments, follow-ups,
    historical agreements and conversion into sales.
-   **Internal payroll:** employee records and internal payment
    statements (payslips) in PDF.
-   **Notifications:** scheduled and manual payment reminders through
    email and WhatsApp.
-   **Reports:** PDF, XLSX and CSV exports.
-   **Access control:** users, roles, permissions and audit history.

## Repository structure

``` text
financial-management-system/
├── frontend/                  # Next.js application
├── backend/                   # Spring Boot application
├── infrastructure/
│   ├── docker/                # Container-related configuration
│   └── database/              # Optional database support files
├── docs/
│   ├── architecture.md
│   ├── requirements.md
│   ├── database.md
│   ├── api.md
│   ├── roadmap.md
│   └── development.md
├── docker-compose.yml
├── .env.example
├── .gitignore
└── README.md
```

The folders and files above describe the intended repository layout;
create them as implementation work proceeds.

## Quick start (target development workflow)

Prerequisites: - Git - Docker Desktop or Docker Engine with Compose -
Java 21 or the selected compatible LTS version - Node.js LTS and npm

Once the backend, frontend and Compose configuration have been
implemented:

``` bash
git clone <repository-url>
cd financial-management-system
cp .env.example .env
```

Review `.env` and replace all development placeholders. Never use
production credentials in local development.

Start the database:

``` bash
docker compose up -d postgres
```

Run the backend (Windows PowerShell):

``` powershell
cd backend
.\mvnw.cmd spring-boot:run
```

Run the backend (Linux/macOS):

``` bash
cd backend
./mvnw spring-boot:run
```

Run the frontend in a second terminal:

``` bash
cd frontend
npm install
npm run dev
```

Expected local URLs (subject to the implemented configuration): -
Frontend: http://localhost:3000 - Backend: http://localhost:8080 - API
base path: `/api/v1` - Swagger UI: typically `/swagger-ui/index.html`

Database migrations should run through Flyway during backend startup or
an explicitly documented migration command.

## Core financial rules

1.  Use `BigDecimal` in Java and `NUMERIC`/`DECIMAL` in PostgreSQL for
    monetary values. Never use `float` or `double` for money.
2.  A sale, an account receivable and a payment are distinct records.
3.  Support partial payments and calculate outstanding balances from
    persisted payment allocations.
4.  Use database transactions for operations that update related
    financial records.
5.  Prevent duplicate payment processing with idempotency controls and
    appropriate database constraints.
6.  Preserve the audit history of financial operations. Cancel or
    reverse posted transactions rather than physically deleting them.
7.  Distinguish cash-basis reports (actual receipts and disbursements)
    from accrual-basis reports (recognized income and expenses).
8.  Do not trust frontend totals or calculations as authoritative.
    Recalculate and validate financial operations on the backend.

## Security and privacy

-   Enforce authentication and role-based authorization on the backend.
-   Hash passwords using a modern password-hashing algorithm such as
    BCrypt or Argon2.
-   Protect refresh tokens and implement rotation and revocation.
-   Validate and authorize file uploads.
-   Keep secrets out of source control; use environment variables or a
    secret manager.
-   Use HTTPS in deployed environments.
-   Apply rate limiting and secure error handling.
-   Maintain audit logs for sensitive operations.
-   Design personal-data processing around applicable Brazilian LGPD
    requirements.

## Payroll scope and legal boundary

The initial payroll module creates **internal employee payment
statements**. It is not an official Brazilian payroll, eSocial, tax
calculation or CLT compliance engine. Statutory calculations such as
INSS, IRRF and FGTS must not be represented as legally compliant unless
implemented from validated, current rules and reviewed by qualified
specialists.

## Notifications and external services

Email and WhatsApp integrations must be isolated behind provider
interfaces so providers can be changed without rewriting finance logic.

WhatsApp integration must use the official WhatsApp Business Platform
and follow its template, opt-in, messaging-window and policy
requirements. External providers may charge fees. Do not use unofficial
WhatsApp automation libraries.

Reminder processing should run asynchronously, record delivery attempts
and provider identifiers, retry transient failures with limits and
backoff, and avoid sending reminders for paid or cancelled receivables.

## Development approach

Build and verify the system incrementally:

1.  Foundation, database, authentication and company settings.
2.  Customers, products, inventory and sales.
3.  Financial management and dashboard.
4.  Negotiation history and follow-ups.
5.  Email and WhatsApp reminders.
6.  Internal payroll and PDF payslips.
7.  Reports and exports.
8.  Security review, testing, backups and deployment.

See [Roadmap](docs/roadmap.md) for phase deliverables and acceptance
criteria.

## Definition of done

A feature is complete only when its UI, backend behavior, persistence,
validation, authorization, error handling and automated tests are
implemented. External integrations must be verified against a real
sandbox or test environment. Mock behavior must be clearly identified
and must never be presented as a completed integration.

## Contributing

Before implementing a feature: 1. Read the relevant documentation under
`docs/`. 2. Confirm its data model and API contract. 3. Add or update
Flyway migrations. 4. Implement backend business rules and
authorization. 5. Implement frontend workflows and validation. 6. Add
automated tests. 7. Update the documentation.

Keep pull requests focused and document architectural decisions that
affect data integrity, security or future compatibility.

## License

No license has been selected yet. Add a `LICENSE` file before
distributing or accepting external contributions. Do not assume that
this project is open source until a license is explicitly chosen.
