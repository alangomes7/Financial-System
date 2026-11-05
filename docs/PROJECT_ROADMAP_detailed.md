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


------------------------------------------------------------------------

## 9. Granular task breakdown

Use these smaller checkboxes to track implementation work. The original
phase tasks above remain as scope summaries. Keep an original phase task
open until all relevant subtasks below are complete. Existing completed
(`[x]`) items in the original roadmap are intentionally preserved.

### Phase 0 --- Requirements, domain rules, and architecture

#### Scope and decisions
- [ ] Read the root `README.md`.
- [ ] List the files currently present in `docs/`.
- [ ] Review each relevant document in `docs/`.
- [ ] Create a list of features included in the MVP.
- [ ] Create a list of features explicitly deferred from the MVP.
- [ ] Record the initial user roles.
- [ ] List the actions each role is allowed to perform.
- [ ] Confirm the business timezone is `America/Sao_Paulo`.
- [ ] Confirm the default currency is `BRL`.
- [ ] Document currency formatting and decimal precision.
- [ ] Write a glossary entry for sale.
- [ ] Write a glossary entry for receivable.
- [ ] Write a glossary entry for payable.
- [ ] Write a glossary entry for expense.
- [ ] Write a glossary entry for payment.
- [ ] Write a glossary entry for cash movement.
- [ ] Decide whether inventory quantity tracking is required in the MVP.
- [ ] Define when a transaction can be cancelled.
- [ ] Define when a posted transaction must be reversed.
- [ ] Define how refunds are represented.
- [ ] Define how write-offs are represented.
- [ ] Decide whether each report uses cash basis, accrual basis, or both.
- [ ] Record unresolved questions and assumptions.

#### Architecture and approval
- [ ] Draft the initial system context diagram.
- [ ] Draft the initial backend module/package structure.
- [ ] Draft the initial entity relationship diagram.
- [ ] Document the sale-to-receivable-to-payment lifecycle.
- [ ] Document valid financial status transitions.
- [ ] Write ADRs for decisions that affect architecture.
- [ ] Prioritize the MVP backlog.
- [ ] Review and approve the requirements and architecture.

### Phase 2 --- Backend foundation and database migrations

#### Backend structure and configuration
- [ ] Inspect the current backend package structure.
- [ ] Create packages organized by business domain or feature.
- [ ] Add/configure Spring Web.
- [ ] Add/configure Spring Data JPA.
- [ ] Add/configure Bean Validation.
- [ ] Add/configure Spring Security.
- [ ] Move database connection values to environment variables.
- [ ] Document required database environment variables.
- [ ] Configure Flyway in the application.
- [ ] Configure migration locations and naming conventions.
- [ ] Create the first Flyway migration.
- [ ] Verify the migration against an empty PostgreSQL database.
- [ ] Disable Hibernate schema auto-update for production profiles.

#### API conventions and observability
- [ ] Define the standard success response format.
- [ ] Define the standard API error response format.
- [ ] Implement global exception handling.
- [ ] Add validation for request DTOs.
- [ ] Map validation failures to consistent HTTP status codes.
- [ ] Define pagination request parameters.
- [ ] Define sorting request parameters.
- [ ] Define filtering conventions.
- [ ] Add Springdoc/OpenAPI.
- [ ] Verify the OpenAPI page is accessible.
- [ ] Configure structured application logging.
- [ ] Review logs to ensure secrets are not exposed.

#### Database conventions and tests
- [ ] Choose and document the primary-key strategy.
- [ ] Define the monetary precision and scale.
- [ ] Define timestamp storage conventions.
- [ ] Configure business-timezone display behavior.
- [ ] Define common audit columns.
- [ ] Add foreign keys for core relationships.
- [ ] Add indexes for expected lookup paths.
- [ ] Create a test profile and test database configuration.
- [ ] Add a unit-test example for a service.
- [ ] Add an integration-test example for a repository/API.
- [ ] Test migration execution on a clean database.
- [ ] Test startup failure when required configuration is missing.
- [ ] Test global validation and exception handling.

### Phase 3 --- Authentication, authorization, and company settings

#### Identity and authentication
- [ ] Define the user entity fields.
- [ ] Define the role entity and user-role relationship.
- [ ] Create the user database migration.
- [ ] Create the role database migration.
- [ ] Implement password hashing with BCrypt or Argon2.
- [ ] Implement the login endpoint.
- [ ] Implement access-token generation and validation.
- [ ] Implement refresh-token generation.
- [ ] Implement refresh-token rotation.
- [ ] Implement refresh-token revocation.
- [ ] Implement logout/token revocation behavior.
- [ ] Add rate limiting to login and token endpoints.
- [ ] Ensure passwords and tokens are never logged.

#### Authorization and company settings
- [ ] Configure backend endpoints to require authentication by default.
- [ ] Implement role-based authorization rules.
- [ ] Add user activation.
- [ ] Add user deactivation.
- [ ] Create the company settings entity.
- [ ] Add legal name and display name fields.
- [ ] Add currency and timezone fields.
- [ ] Add company contact fields.
- [ ] Create the company settings migration.
- [ ] Implement company settings read/update endpoints.
- [ ] Record audit events for login and logout.
- [ ] Record audit events for permission-sensitive actions.

#### Frontend and security verification
- [ ] Build the login page.
- [ ] Connect the login form to the backend.
- [ ] Implement frontend logout.
- [ ] Implement protected-route behavior.
- [ ] Display authentication errors safely.
- [ ] Test unauthenticated access to protected endpoints.
- [ ] Test allowed actions for each role.
- [ ] Test denied actions for each role.
- [ ] Verify backend authorization works independently of UI visibility.

### Phase 4 --- Core master data

#### Customers
- [ ] Define customer fields and required/optional rules.
- [ ] Create the customer Flyway migration.
- [ ] Implement customer creation.
- [ ] Implement customer details retrieval.
- [ ] Implement customer update.
- [ ] Implement customer deactivation.
- [ ] Add customer search.
- [ ] Add customer filters.
- [ ] Add customer sorting and pagination.
- [ ] Validate required customer fields.
- [ ] Validate unique identifiers when applicable.
- [ ] Build the customer list screen.
- [ ] Build the customer detail screen.
- [ ] Build the customer create/edit form.

#### Suppliers
- [ ] Define supplier fields and required/optional rules.
- [ ] Create the supplier Flyway migration.
- [ ] Implement supplier creation.
- [ ] Implement supplier details retrieval.
- [ ] Implement supplier update.
- [ ] Implement supplier deactivation.
- [ ] Add supplier search and filters.
- [ ] Add supplier pagination and sorting.
- [ ] Build the supplier list screen.
- [ ] Build the supplier create/edit form.

#### Products and services
- [ ] Define product and service fields.
- [ ] Decide the SKU/code uniqueness rule.
- [ ] Define supported units of measure.
- [ ] Create the product/service migration.
- [ ] Implement product creation and retrieval.
- [ ] Implement product update and deactivation.
- [ ] Implement service creation and retrieval.
- [ ] Implement service update and deactivation.
- [ ] Store description, code/SKU, unit, price, and active status.
- [ ] Confirm whether tax-related fields are in scope.
- [ ] Keep inventory tracking separate unless approved for the MVP.
- [ ] Build product/service list and form screens.

#### Shared frontend and quality
- [ ] Create reusable form controls.
- [ ] Add client-side form validation.
- [ ] Add server-side validation error display.
- [ ] Implement loading states.
- [ ] Implement empty states.
- [ ] Implement error states.
- [ ] Test CRUD permissions for each master-data module.
- [ ] Test search and pagination.
- [ ] Verify deactivated records remain available to historical transactions.

### Phase 5 --- Sales and accounts receivable

#### Domain model and sale creation
- [ ] Define sale fields and lifecycle statuses.
- [ ] Define sale-item fields and relationships.
- [ ] Create sale and sale-item migrations.
- [ ] Define which customer states permit a sale.
- [ ] Define which product/service states permit a sale.
- [ ] Implement sale creation request validation.
- [ ] Implement customer validation during sale creation.
- [ ] Implement item and quantity validation.
- [ ] Calculate line totals on the backend.
- [ ] Calculate discounts on the backend.
- [ ] Calculate additional charges on the backend.
- [ ] Calculate the final sale total on the backend.
- [ ] Add tests for total calculations and rounding.

#### Receivables and installments
- [ ] Define receivable fields and statuses.
- [ ] Create the receivable migration.
- [ ] Define installment schedule fields and rules.
- [ ] Implement receivable generation from a sale.
- [ ] Implement single-payment-term receivables.
- [ ] Implement installment-based receivables.
- [ ] Validate due dates and installment amounts.
- [ ] Define valid receivable status transitions.
- [ ] Prevent invalid status transitions in the service layer.
- [ ] Wrap sale and receivable creation in one database transaction.

#### Frontend and verification
- [ ] Build the sale creation screen.
- [ ] Add customer selection to the sale form.
- [ ] Add product/service selection to the sale form.
- [ ] Add quantity and price entry.
- [ ] Display backend-calculated totals.
- [ ] Build the sales list and detail screens.
- [ ] Build the receivables list and detail screens.
- [ ] Test that a valid sale creates the expected receivables.
- [ ] Test that invalid sales leave no partial records.
- [ ] Test receivable balances and status transitions.

### Phase 6 --- Expenses and accounts payable

- [ ] Define expense fields and categories.
- [ ] Define payable fields and statuses.
- [ ] Create expense and payable migrations.
- [ ] Define the relationship between an expense and its payable.
- [ ] Implement expense creation.
- [ ] Implement expense retrieval and update rules.
- [ ] Implement supplier association.
- [ ] Implement issue-date and due-date validation.
- [ ] Implement expense category selection.
- [ ] Implement payable creation from an expense.
- [ ] Decide whether recurring expenses are included in the MVP.
- [ ] If approved, define recurring expense frequency and generation rules.
- [ ] Implement payable status transitions.
- [ ] Implement full payment registration against a payable.
- [ ] Implement partial payment registration against a payable.
- [ ] Define permitted attachment file types.
- [ ] Define maximum attachment size.
- [ ] Implement attachment upload validation.
- [ ] Enforce attachment access permissions.
- [ ] Build expense list and detail screens.
- [ ] Build expense create/edit forms.
- [ ] Build payable list and detail screens.
- [ ] Test full and partial payable balances.
- [ ] Test that cancelled records remain in financial history.
- [ ] Test attachment validation and authorization.

### Phase 7 --- Payments, cash flow, and financial integrity

#### Payment model and posting
- [ ] Define payment fields and supported payment methods.
- [ ] Define payment relationships to receivables and payables.
- [ ] Create the payment migration.
- [ ] Implement payment creation validation.
- [ ] Implement payment posting for receivables.
- [ ] Implement payment posting for payables.
- [ ] Support full settlement.
- [ ] Support partial settlement.
- [ ] Reject overpayments unless an approved workflow permits them.
- [ ] Wrap payment posting in a database transaction.
- [ ] Add idempotency keys or an equivalent duplicate-prevention strategy.
- [ ] Test duplicate payment requests.
- [ ] Define cash account/payment-channel representation.

#### Reversals, refunds, and reconciliation
- [ ] Define reversal reasons and required metadata.
- [ ] Implement payment reversal.
- [ ] Define refund behavior and its relationship to the original payment.
- [ ] Implement refund recording if in MVP scope.
- [ ] Record user and timestamp for reversals/refunds.
- [ ] Emit audit events for reversals/refunds.
- [ ] Implement receivable balance calculation from payment records.
- [ ] Implement payable balance calculation from payment records.
- [ ] Implement reconciliation checks between balances and payments.
- [ ] Protect concurrent payment posting from race conditions.
- [ ] Test rounding and decimal precision.

#### Cash flow
- [ ] Define cash-flow inclusion rules.
- [ ] Confirm the cash/accrual basis used by each view.
- [ ] Implement cash inflow queries.
- [ ] Implement cash outflow queries.
- [ ] Implement date-range filtering.
- [ ] Implement cash-flow summary calculations.
- [ ] Test that cash-flow totals reconcile with posted transactions.
- [ ] Verify every balance can be explained by source transactions.

### Phase 8 --- Frontend dashboard and end-to-end workflows

- [ ] Create the authenticated application shell.
- [ ] Implement primary navigation.
- [ ] Implement responsive page layout.
- [ ] Define dashboard metric formulas and date semantics.
- [ ] Implement receivables dashboard card.
- [ ] Implement payables dashboard card.
- [ ] Implement overdue amounts dashboard card.
- [ ] Implement cash position dashboard card.
- [ ] Add dashboard date-range filters.
- [ ] Connect dashboard cards to backend endpoints.
- [ ] Implement customer workflows end to end.
- [ ] Implement supplier workflows end to end.
- [ ] Implement product/service workflows end to end.
- [ ] Implement sales workflows end to end.
- [ ] Implement expense workflows end to end.
- [ ] Implement payment workflows end to end.
- [ ] Add table pagination.
- [ ] Add table sorting.
- [ ] Add table filters.
- [ ] Add accessible form labels and keyboard behavior.
- [ ] Add confirmation dialogs for irreversible actions.
- [ ] Implement loading, empty, validation, and API error states.
- [ ] Add end-to-end tests for the main user journeys.
- [ ] Compare dashboard figures with backend reports using identical filters.
- [ ] Verify agreed responsive screen sizes.

### Phase 9 --- Negotiations, collection history, and reminders

#### Negotiation history
- [ ] Define negotiation fields and statuses.
- [ ] Create the negotiation migration.
- [ ] Link negotiations to customers.
- [ ] Link negotiations to receivables when applicable.
- [ ] Implement negotiation creation and editing rules.
- [ ] Store notes, interaction dates, responsible users, and outcomes.
- [ ] Store next follow-up dates.
- [ ] Display a chronological interaction timeline.
- [ ] Implement controlled attachment access.
- [ ] Add filters for pending follow-ups.
- [ ] Add filters for overdue negotiations.
- [ ] Test negotiation history retrieval by customer and status.

#### Reminder rules and delivery
- [ ] Define reminder configuration fields.
- [ ] Confirm the default reminder schedule.
- [ ] Implement the reminder rule for 3 days before due date.
- [ ] Implement the reminder rule on the due date.
- [ ] Implement the reminder rule 3 days after due date.
- [ ] Implement the reminder rule 7 days after due date.
- [ ] Implement manual reminder triggering.
- [ ] Select and configure an email provider.
- [ ] Implement email template rendering.
- [ ] Send a test email through the provider.
- [ ] Confirm WhatsApp Business account and API prerequisites.
- [ ] Confirm WhatsApp template approval and consent requirements.
- [ ] Implement WhatsApp integration only after prerequisites are approved.
- [ ] Persist each delivery attempt and provider response.
- [ ] Persist timestamps and failure reasons.
- [ ] Implement retry with backoff.
- [ ] Implement reminder deduplication.
- [ ] Exclude paid receivables.
- [ ] Exclude cancelled or otherwise ineligible receivables.
- [ ] Implement notification preferences and applicable opt-out handling.
- [ ] Display delivery history to authorized users.
- [ ] Test duplicate prevention across retries and scheduler runs.
- [ ] Test safe retry after provider failures.

### Phase 10 --- Internal payroll statements and reporting

#### Internal payroll statements
- [ ] Confirm the exact fields required in an internal statement.
- [ ] Define employee/recipient records.
- [ ] Define payment-period records.
- [ ] Define configurable internal calculation inputs.
- [ ] Create the required database migrations.
- [ ] Implement statement record creation.
- [ ] Implement statement retrieval.
- [ ] Generate a PDF statement.
- [ ] Restrict payroll data by role.
- [ ] Record statement generation history.
- [ ] Record correction history without silently overwriting prior statements.
- [ ] Add UI wording that identifies statements as internal documents.
- [ ] Document that statutory payroll calculations are out of scope.

#### Reports and exports
- [ ] Define filters for receivables reports.
- [ ] Implement receivables by status and due date.
- [ ] Define filters for payables reports.
- [ ] Implement payables by status and due date.
- [ ] Implement income summaries.
- [ ] Implement expense summaries.
- [ ] Implement cash-flow report.
- [ ] Implement sales report.
- [ ] Implement customer summary report.
- [ ] Implement supplier summary report.
- [ ] Implement PDF export.
- [ ] Implement XLSX export.
- [ ] Implement CSV export.
- [ ] Enforce permissions on report endpoints.
- [ ] Enforce permissions on generated files.
- [ ] Test date filters and report totals.
- [ ] Compare exported totals with the filtered on-screen results.
- [ ] Test PDF statement and report rendering.

### Phase 11 --- Production hardening and MVP release

#### Security and privacy
- [ ] Configure HTTPS for production.
- [ ] Review authentication configuration.
- [ ] Review authorization rules and object-level access.
- [ ] Review session and token handling.
- [ ] Review rate limits.
- [ ] Validate all API inputs.
- [ ] Validate uploaded file content and size.
- [ ] Review protection against injection.
- [ ] Review protection against insecure direct object references.
- [ ] Review access to customer and financial data.
- [ ] Review application logs for sensitive data exposure.
- [ ] Review data retention and consent requirements.
- [ ] Obtain appropriate review of LGPD obligations.
- [ ] Scan application dependencies.
- [ ] Scan container images.
- [ ] Resolve critical and high-severity findings.

#### Reliability and operations
- [ ] Configure automated database backups.
- [ ] Document backup frequency and retention.
- [ ] Perform a database restore test.
- [ ] Configure application health checks.
- [ ] Configure application monitoring.
- [ ] Centralize application logs.
- [ ] Define alerts for critical failures.
- [ ] Configure production environment variables.
- [ ] Configure production secrets management.
- [ ] Document deployment steps.
- [ ] Document rollback steps.
- [ ] Test migrations against a production-like database.
- [ ] Document support procedures.
- [ ] Document incident-response procedures.

#### Release
- [ ] Review all MVP acceptance criteria.
- [ ] Run the full critical automated test suite in CI.
- [ ] Confirm no unresolved critical/high security issues.
- [ ] Confirm backup and restore testing is successful.
- [ ] Rehearse production deployment.
- [ ] Rehearse rollback.
- [ ] Publish user documentation.
- [ ] Publish operational runbooks.
- [ ] Approve the MVP release.
