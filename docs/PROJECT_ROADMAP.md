# Financial Management System --- Project Construction Roadmap

> **Purpose:** provide an incremental, testable construction plan for
> the financial management system.\
> **Initial deployment model:** one company.\
> **Future direction:** keep the architecture extensible for
> multi-tenant SaaS, without implementing multi-tenancy in the MVP.\
> **Primary stack:** Next.js + TypeScript, Java + Spring Boot,
> PostgreSQL, Flyway, Docker Compose.

------------------------------------------------------------------------

## 1. Project goals and delivery principles

Build a secure, maintainable financial management application for a
single company, with a clear path to future expansion.

### Core product capabilities

-   User authentication, roles, and permissions.
-   Customer and supplier management.
-   Product and service catalog.
-   Sales and receivables.
-   Expenses, payables, and payments, including partial payments.
-   Cash-flow overview and financial dashboard.
-   Negotiation and collection history.
-   Email and WhatsApp payment reminders.
-   Internal payroll/payment statements.
-   Reports and exports (PDF, XLSX, CSV).
-   Audit trail and operational safeguards.

### Engineering principles

1.  Deliver in small, working increments.
2.  Design the data model and financial rules before building dependent
    screens.
3.  Keep financial calculations on the backend.
4.  Use `BigDecimal` in Java and `NUMERIC/DECIMAL` in PostgreSQL for
    monetary values.
5.  Use database transactions for operations that change multiple
    financial records.
6.  Preserve financial history: reverse or cancel records instead of
    silently deleting them.
7.  Make APIs explicit, validated, documented, and versionable.
8.  Automate tests, migrations, formatting, and builds.
9.  Do not implement legal payroll calculations or statutory reporting
    without validated requirements and specialist review.

------------------------------------------------------------------------

## 2. High-level construction sequence

  -------------------------------------------------------------------------
  Phase             Focus             Main deliverable    Exit condition
  ----------------- ----------------- ------------------- -----------------
  0                 Requirements and  Approved scope and  Open questions
                    decisions         architecture        and assumptions
                                                          documented

  1                 Repository and    Running frontend,   A developer can
                    development       backend, and        start the full
                    environment       PostgreSQL          stack locally

  2                 Backend           Spring Boot         Health checks,
                    foundation        baseline and        migrations,
                                      database migrations validation, and
                                                          API docs work

  3                 Authentication    Secure login and    Protected
                    and access        role-based access   endpoints enforce
                    control                               permissions

  4                 Core master data  Customers,          CRUD flows work
                                      suppliers,          with validation
                                      products, services  and tests

  5                 Sales and         Sales,              A sale can create
                    receivables       invoices/charges,   and track an
                                      receivables         amount due

  6                 Payables and      Expenses, payable   Expenses and
                    expenses          items, payments     payment status
                                                          are traceable

  7                 Financial         Partial payments,   Balances and
                    operations        reversals, cash     cash-flow
                                      flow                calculations
                                                          reconcile

  8                 Frontend          Dashboard and       Main user
                    application       operational screens journeys work end
                                                          to end

  9                 Negotiations and  Follow-up history   Reminders are
                    reminders         and notifications   safe, traceable,
                                                          and configurable

  10                Payroll           Internal statements Outputs are
                    statements and    and exports         tested and
                    reports                               clearly scoped

  11                Hardening and     Production-ready    Security, backup,
                    release           MVP                 monitoring, and
                                                          deployment checks
                                                          pass
  -------------------------------------------------------------------------

The sequence is a dependency guide, not a requirement to postpone all
frontend work. Build a minimal frontend shell early, then implement each
business workflow as a vertical slice across frontend, API, and
database.

------------------------------------------------------------------------

## 3. Detailed phase plan

## Phase 0 --- Requirements, domain rules, and architecture

### Tasks

-   [ ] Review `README.md` and all documents in `docs/`.
-   [ ] Confirm the MVP scope and explicitly defer non-MVP features.
-   [ ] Define user roles and permissions.
-   [ ] Define the company's financial calendar and timezone
    (`America/Sao_Paulo`).
-   [ ] Confirm currency (`BRL`) and display conventions.
-   [ ] Define the meaning of sale, receivable, payable, expense,
    payment, and cash movement.
-   [ ] Decide whether the first release needs inventory quantity
    tracking.
-   [ ] Define cancellation, reversal, refund, and write-off rules.
-   [ ] Define whether reports use cash basis, accrual basis, or both.
-   [ ] Document assumptions and unresolved questions.

### Deliverables

-   Approved MVP requirements.
-   Domain glossary.
-   Initial architecture decision records (ADRs).
-   Initial entity relationship diagram.

### Exit criteria

-   No unresolved ambiguity in the core sale-to-receivable-to-payment
    flow.
-   Financial status transitions are documented.
-   The MVP backlog is prioritized.

------------------------------------------------------------------------

## Phase 1 --- Repository and local development environment

### Tasks

-   [x] Create a Git repository with `frontend/`, `backend/`,
    `infrastructure/`, and `docs/`.
-   [x] Add root-level `.gitignore`, `.editorconfig`, and
    `.env.example`.
-   [x] Add Docker Compose for PostgreSQL.
-   [x] Configure persistent database storage and health checks.
-   [x] Create the Next.js application with TypeScript.
-   [x] Create the Spring Boot application with the selected Java LTS
    version.
-   [x] Add a backend health endpoint.
-   [x] Add frontend and backend build instructions.
-   [x] Add a basic CI workflow for builds and tests.

### Suggested repository layout

``` text
financial-management-system/
├── frontend/
├── backend/
├── infrastructure/
│   ├── docker/
│   └── database/
├── docs/
├── docker-compose.yml
├── .env.example
├── .gitignore
└── README.md
```

### Exit criteria

-   [x] PostgreSQL starts through Docker Compose.
-   [x] Backend starts locally and connects to PostgreSQL.
-   [x] Frontend starts locally.
-   [x] No secrets are committed to Git.
-   [x] A clean checkout can be built using documented instructions.

------------------------------------------------------------------------

## Phase 2 --- Backend foundation and database migrations

### Tasks

-   [ ] Organize backend packages by business domain or feature.
-   [ ] Configure Spring Web, Spring Data JPA, Bean Validation, and
    Spring Security.
-   [ ] Configure PostgreSQL connection settings through environment
    variables.
-   [ ] Configure Flyway and create the first migration.
-   [ ] Establish common API response and error-handling conventions.
-   [ ] Add request validation and consistent HTTP status codes.
-   [ ] Add pagination, sorting, and filtering conventions.
-   [ ] Generate OpenAPI documentation with Springdoc.
-   [ ] Add structured application logging.
-   [ ] Add unit and integration test conventions.

### Initial database conventions

-   Use primary keys consistently (UUID is a reasonable default).
-   Use `NUMERIC(19,2)` or an explicitly chosen precision for monetary
    values.
-   Store timestamps consistently; display them using the configured
    business timezone.
-   Add `created_at`, `updated_at`, and, where appropriate, `created_by`
    and `updated_by`.
-   Use foreign keys and indexes for important relationships and lookup
    paths.
-   Use migrations for every schema change; do not rely on Hibernate
    schema auto-update in production.

### Exit criteria

-   [ ] Flyway applies migrations to a clean database.
-   [ ] Application fails clearly when required configuration is
    missing.
-   [ ] API documentation is accessible.
-   [ ] Global validation and exception handling are tested.

------------------------------------------------------------------------

## Phase 3 --- Authentication, authorization, and company settings

### Tasks

-   [ ] Create user and role models.
-   [ ] Implement login and secure password hashing (BCrypt or Argon2).
-   [ ] Implement access-token and refresh-token handling, including
    rotation and revocation.
-   [ ] Protect backend endpoints by default.
-   [ ] Implement role-based access control (RBAC).
-   [ ] Add user activation/deactivation.
-   [ ] Create company settings for legal name, display name, currency,
    timezone, and contact details.
-   [ ] Add audit events for authentication and permission-sensitive
    actions.
-   [ ] Build frontend login, logout, and protected-route behavior.

### Security checks

-   [ ] Passwords are never stored or logged in plain text.
-   [ ] Tokens are not exposed in URLs or application logs.
-   [ ] Authorization is enforced by the backend, not only by frontend
    visibility.
-   [ ] Login and token endpoints have appropriate rate limits.
-   [ ] Secrets are supplied through environment variables or a secret
    manager.

### Exit criteria

-   [ ] Unauthenticated users cannot access protected endpoints.
-   [ ] Users cannot perform actions outside their assigned permissions.
-   [ ] Login and logout work in the frontend.
-   [ ] Security tests cover representative allowed and denied actions.

------------------------------------------------------------------------

## Phase 4 --- Core master data

Implement these modules before sales so transactions can reference
consistent records.

### Customers

-   [ ] Create, view, update, and deactivate customers.
-   [ ] Store contact information and optional billing details.
-   [ ] Support search, filters, sorting, and pagination.
-   [ ] Validate required fields and unique identifiers where
    applicable.

### Suppliers

-   [ ] Create, view, update, and deactivate suppliers.
-   [ ] Store contact and payment details.
-   [ ] Support search and filtering.

### Products and services

-   [ ] Create products and services.
-   [ ] Store description, code/SKU, unit, price, and active status.
-   [ ] Define tax-related fields only when requirements are confirmed.
-   [ ] Add optional inventory tracking as a separate capability.

### Frontend tasks

-   [ ] Create list, detail, create, and edit screens.
-   [ ] Add reusable form controls and validation.
-   [ ] Provide clear empty, loading, and error states.

### Exit criteria

-   [ ] CRUD operations work through the API and UI.
-   [ ] Validation and permission tests pass.
-   [ ] Records can be searched and paginated.
-   [ ] Deactivation does not break historical transactions.

------------------------------------------------------------------------

## Phase 5 --- Sales and accounts receivable

### Domain rules to settle first

-   A sale is not the same thing as a payment.
-   A receivable represents an amount expected to be collected.
-   A payment records money actually received.
-   A sale may generate one or more receivable installments.
-   Partial payments must reduce the outstanding balance without losing
    payment history.

### Tasks

-   [ ] Define sale and sale-item entities.
-   [ ] Implement sale creation with customer and item validation.
-   [ ] Calculate totals on the backend.
-   [ ] Define discounts and additional charges.
-   [ ] Generate receivable records from sales.
-   [ ] Support due dates and installment schedules.
-   [ ] Track statuses such as draft, open, partially paid, paid,
    overdue, cancelled, and written off, as appropriate.
-   [ ] Prevent invalid state transitions.
-   [ ] Add transaction boundaries so sale and receivable creation
    succeed or fail together.
-   [ ] Build sales and receivables screens.

### Exit criteria

-   [ ] A valid sale creates the expected receivable records.
-   [ ] Invalid sales do not leave partial database records.
-   [ ] Totals are calculated and validated on the backend.
-   [ ] Receivable balances and statuses are covered by automated tests.

------------------------------------------------------------------------

## Phase 6 --- Expenses and accounts payable

### Tasks

-   [ ] Define expense and payable entities.
-   [ ] Record expense category, supplier, amount, issue date, and due
    date.
-   [ ] Support recurring expenses only if included in the approved MVP.
-   [ ] Track open, partially paid, paid, overdue, and cancelled states.
-   [ ] Register payments against payables.
-   [ ] Support attachments with file type, size, and access controls.
-   [ ] Build expense and payable screens.

### Exit criteria

-   [ ] Expenses can be recorded and linked to payables.
-   [ ] Payable balances are accurate after full and partial payments.
-   [ ] Historical records remain available after cancellation.
-   [ ] Attachments are access-controlled and validated.

------------------------------------------------------------------------

## Phase 7 --- Payments, cash flow, and financial integrity

This phase is critical. Do not treat a status update as a substitute for
a financial transaction.

### Tasks

-   [ ] Implement a payment entity with amount, date, method, reference,
    and related receivable/payable.
-   [ ] Support full and partial payments.
-   [ ] Validate that payment amounts do not exceed outstanding balances
    unless an explicit overpayment/refund workflow is designed.
-   [ ] Use database transactions for payment posting.
-   [ ] Implement reversal/refund workflows with reason, user,
    timestamp, and audit event.
-   [ ] Make payment posting idempotent to prevent duplicate processing.
-   [ ] Define how cash accounts or payment channels are represented.
-   [ ] Implement cash-flow queries and summaries.
-   [ ] Reconcile receivable/payable balances against their payment
    records.

### Required financial tests

-   [ ] Full payment closes the balance.
-   [ ] Partial payment reduces the balance correctly.
-   [ ] Multiple partial payments produce the correct remaining balance.
-   [ ] Duplicate requests do not duplicate payments.
-   [ ] Reversal restores the correct outstanding balance.
-   [ ] Concurrent payment attempts cannot overpay due to race
    conditions.
-   [ ] Rounding and decimal precision are consistent.

### Exit criteria

-   [ ] Every balance can be explained from its source transactions.
-   [ ] Financial operations are atomic and auditable.
-   [ ] Cash-flow totals reconcile with posted transactions under the
    documented accounting basis.

------------------------------------------------------------------------

## Phase 8 --- Frontend dashboard and end-to-end workflows

Build the frontend around working backend capabilities.

### Tasks

-   [ ] Establish application layout, navigation, and responsive
    behavior.
-   [ ] Create dashboard cards for receivables, payables, overdue
    amounts, and cash position.
-   [ ] Add date-range filters and clear definitions for each metric.
-   [ ] Build customer, supplier, product, sales, expense, and payment
    workflows.
-   [ ] Add tables with pagination, sorting, and filters.
-   [ ] Add accessible forms and confirmation dialogs for destructive or
    irreversible actions.
-   [ ] Handle loading, empty, validation, and API error states.
-   [ ] Add end-to-end tests for the main user journeys.

### Exit criteria

-   [ ] A user can complete the core workflows without using API tools
    directly.
-   [ ] Dashboard figures match backend reports for the same filters.
-   [ ] Responsive layouts work at the agreed screen sizes.
-   [ ] Critical workflows have end-to-end coverage.

------------------------------------------------------------------------

## Phase 9 --- Negotiations, collection history, and reminders

### Negotiation and follow-up tasks

-   [ ] Create negotiation records linked to customers and receivables.
-   [ ] Store notes, dates, responsible users, outcomes, and next
    follow-up dates.
-   [ ] Preserve a chronological interaction history.
-   [ ] Support controlled attachment access.
-   [ ] Add filters for pending follow-ups and overdue negotiations.

### Reminder tasks

-   [ ] Implement configurable reminder rules.
-   [ ] Suggested defaults: 3 days before due date, on the due date, 3
    days overdue, and 7 days overdue.
-   [ ] Allow manual reminders.
-   [ ] Add email integration.
-   [ ] Add WhatsApp Business Cloud API integration only after account,
    template, consent, and policy requirements are confirmed.
-   [ ] Store delivery attempts, provider responses, timestamps, and
    failure reasons.
-   [ ] Add retries with backoff and deduplication.
-   [ ] Do not send reminders for paid, cancelled, or otherwise
    ineligible receivables.
-   [ ] Provide notification preferences and opt-out handling where
    applicable.

### Exit criteria

-   [ ] Reminders are not duplicated by retries or repeated scheduler
    runs.
-   [ ] Delivery history is visible to authorized users.
-   [ ] Paid and cancelled receivables are excluded.
-   [ ] Failures can be investigated and safely retried.

------------------------------------------------------------------------

## Phase 10 --- Internal payroll statements and reporting

### Internal payroll statements

-   [ ] Confirm the exact fields required for internal payment
    statements.
-   [ ] Define employee/recipient records and payment-period records.
-   [ ] Generate PDF statements.
-   [ ] Restrict access to payroll-related information.
-   [ ] Record generation and correction history.

**Scope boundary:** the initial feature is for internal payment
statements only. Do not present it as a compliant Brazilian payroll
engine. CLT calculations, INSS, IRRF, FGTS, eSocial, and other statutory
obligations require separate validated requirements and specialist
review.

### Reports and exports

-   [ ] Receivables by status and due date.
-   [ ] Payables by status and due date.
-   [ ] Income and expense summaries.
-   [ ] Cash-flow report.
-   [ ] Sales report.
-   [ ] Customer and supplier summaries.
-   [ ] Export to PDF, XLSX, and CSV.
-   [ ] Apply permission checks to report data and generated files.
-   [ ] Test totals, date filters, and export consistency.

### Exit criteria

-   [ ] PDF statements and reports render correctly.
-   [ ] Exported totals match the underlying filtered data.
-   [ ] Sensitive reports are accessible only to authorized roles.
-   [ ] Payroll limitations are clearly documented in the UI and
    documentation.

------------------------------------------------------------------------

## Phase 11 --- Production hardening and MVP release

### Security and privacy

-   [ ] Enforce HTTPS in production.
-   [ ] Review authentication, authorization, session/token handling,
    and rate limits.
-   [ ] Validate all inputs and uploaded files.
-   [ ] Protect against injection, insecure direct object references,
    and unauthorized data access.
-   [ ] Avoid logging personal, financial, or authentication secrets.
-   [ ] Review data retention, consent, and LGPD obligations with
    appropriate expertise.
-   [ ] Perform dependency and container image scans.

### Reliability and operations

-   [ ] Configure automated database backups.
-   [ ] Test database restoration, not only backup creation.
-   [ ] Configure health checks and application monitoring.
-   [ ] Centralize logs and define alerting for critical failures.
-   [ ] Configure production environment variables and secrets.
-   [ ] Document deployment and rollback procedures.
-   [ ] Test migrations against a production-like database.
-   [ ] Define support and incident-response procedures.

### Release checklist

-   [ ] All MVP acceptance criteria pass.
-   [ ] Critical automated tests pass in CI.
-   [ ] No known critical or high-severity security issues remain
    unresolved.
-   [ ] Backup and restore have been tested.
-   [ ] Production deployment and rollback have been rehearsed.
-   [ ] User documentation and operational runbooks are available.

------------------------------------------------------------------------

## 4. MVP scope and deferred features

### Recommended MVP

Prioritize these capabilities for the first usable release:

1.  Authentication and roles.
2.  Company settings.
3.  Customers and suppliers.
4.  Products and services.
5.  Sales.
6.  Accounts receivable and payments.
7.  Expenses and accounts payable.
8.  Basic cash-flow dashboard.
9.  Audit history for important financial actions.

### Defer until after the MVP

-   Advanced inventory and stock valuation.
-   Complex recurring billing.
-   Automated bank reconciliation and bank integrations.
-   Advanced accounting or tax compliance.
-   Statutory payroll and eSocial.
-   Multi-tenant SaaS billing and tenant administration.
-   Advanced analytics and forecasting.
-   Complex approval workflows.

Only move a deferred feature into the MVP after its business rules,
compliance needs, and acceptance criteria are documented.

------------------------------------------------------------------------

## 5. Definition of Done

A feature is complete only when all applicable items are satisfied:

-   [ ] Business rules and acceptance criteria are documented.
-   [ ] Database migration is committed.
-   [ ] Backend validation and authorization are implemented.
-   [ ] API documentation is updated.
-   [ ] Unit and integration tests pass.
-   [ ] Frontend states (loading, empty, success, error) are
    implemented.
-   [ ] Accessibility and responsive behavior are checked.
-   [ ] Audit requirements are implemented.
-   [ ] Logs do not expose secrets or unnecessary personal data.
-   [ ] Documentation and examples are updated.
-   [ ] CI build and tests pass.

For financial features, also verify decimal precision, transaction
boundaries, idempotency, reversals, and reconciliation.

------------------------------------------------------------------------

## 6. Suggested issue and branch workflow

### Issue template

For each task, record:

-   **Title**
-   **Business objective**
-   **Scope**
-   **Out of scope**
-   **Dependencies**
-   **API and database impact**
-   **Acceptance criteria**
-   **Test cases**
-   **Security and audit considerations**

### Branch naming

``` text
feature/<short-description>
bugfix/<short-description>
chore/<short-description>
docs/<short-description>
```

### Pull request checklist

-   [ ] Scope is focused and linked to an issue.
-   [ ] Tests cover the changed behavior.
-   [ ] Migrations are backward-conscious and reviewed.
-   [ ] API and documentation changes are included.
-   [ ] No secrets or production data are committed.
-   [ ] Financial changes include reconciliation and reversal
    considerations.

------------------------------------------------------------------------

## 7. Instructions for an AI coding agent

Use the following operating instructions when asking an AI coding agent
to implement the project:

1.  Read the root `README.md` and every relevant file in `docs/` before
    changing code.
2.  Treat this roadmap and the approved requirements as the source of
    truth.
3.  Work on **one phase or one clearly bounded vertical slice at a
    time**.
4.  Before implementation, summarize the scope, assumptions, affected
    files, schema changes, API changes, risks, and test plan.
5.  Ask for approval when a decision changes financial behavior,
    security boundaries, data retention, or the approved scope.
6.  Do not invent tax, payroll, or accounting rules.
7.  Do not generate the entire application in one pass.
8.  Implement migrations, tests, and documentation alongside each
    feature.
9.  Run the relevant tests and builds before reporting completion.
10. At the end of each phase, report completed work, changed files, test
    results, known limitations, and the next recommended task.
11. Keep the application runnable after every phase.
12. Do not mark a task complete when tests have not been run or a
    required integration is only mocked.

### First task for the coding agent

> Read `README.md` and all files in `docs/`. Review this roadmap. Do not
> implement business modules yet. First inspect the repository, identify
> missing decisions or inconsistencies, propose the target architecture
> and initial PostgreSQL entity relationship model, and produce a Phase
> 1 implementation plan. Include risks, assumptions, migration strategy,
> local development commands, and acceptance criteria. Wait for approval
> before making code changes.

------------------------------------------------------------------------

## 8. Project progress tracker

Use this section as the top-level progress checklist. Keep detailed task
status in your issue tracker.

-   [ ] Phase 0 --- Requirements and architecture approved
-   [x] Phase 1 --- Repository and local environment
-   [ ] Phase 2 --- Backend foundation and migrations
-   [ ] Phase 3 --- Authentication and authorization
-   [ ] Phase 4 --- Core master data
-   [ ] Phase 5 --- Sales and receivables
-   [ ] Phase 6 --- Expenses and payables
-   [ ] Phase 7 --- Payments and cash flow
-   [ ] Phase 8 --- Frontend end-to-end workflows
-   [ ] Phase 9 --- Negotiations and reminders
-   [ ] Phase 10 --- Payroll statements and reports
-   [ ] Phase 11 --- Production hardening and MVP release

**Recommended starting point:** complete Phase 0, then Phase 1. Do not
begin with dashboard charts or advanced modules. Establish the domain
model, financial invariants, database migrations, and a runnable
development environment first.
