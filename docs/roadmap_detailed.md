# Development Roadmap

## Delivery strategy

Build a reliable vertical slice at a time. Each phase must include
database persistence, backend rules, frontend workflows, authorization,
tests and documentation. Do not move to the next phase while critical
financial integrity defects remain.

## Phase 1 --- Foundation

**Scope** - Repository and project setup. - Next.js frontend. - Spring
Boot backend. - PostgreSQL. - Docker Compose. - Flyway. - Company
settings. - Authentication and roles. - API error handling and OpenAPI.

**Deliverables** - Application starts locally. - Database migrations
execute. - Users can log in and log out. - Protected endpoints enforce
permissions. - Environment configuration is documented.

**Acceptance** - Fresh environment can be started using documented
instructions. - Authentication and authorization tests pass. - No
secrets are committed.

## Phase 2 --- Customers, products and sales

**Scope** - Customer CRUD and history. - Product and service catalog. -
Optional stock management. - Sales and sale items. - Payment terms and
receivable creation. - Partial payment workflow.

**Acceptance** - A sale can be created for a customer. - Backend
calculates totals. - Payment terms create the correct receivables. -
Partial payments update outstanding balances correctly. - Duplicate
payment requests are safely rejected or handled idempotently.

## Phase 3 --- Financial management

**Scope** - Income and expenses. - Accounts payable and receivable. -
Payment allocation. - Recurring transactions. - Cash flow. - Financial
dashboard.

**Acceptance** - Actual receipts and disbursements are reported
separately. - Cash and accrual views are not confused. - Partial
settlement works. - Cancellation and reversal preserve history. -
Dashboard figures reconcile with underlying records.

## Phase 4 --- Negotiations

**Scope** - Negotiation records. - Notes and chronological timeline. -
Attachments. - Follow-up dates. - Search and filters. - Conversion into
sales.

**Acceptance** - Previous negotiations can be retrieved by customer and
status. - Conversion creates a linked sale without losing the original
proposal. - Duplicate conversion is prevented.

## Phase 5 --- Notifications

**Scope** - Notification templates. - Email provider integration. -
Official WhatsApp Business Cloud API integration. - Scheduled
reminders. - Persistent job processing. - Delivery history and retry
handling.

**Acceptance** - Test messages can be sent through configured
sandbox/test services. - Paid and cancelled receivables are excluded. -
Duplicate reminders are prevented. - Failed attempts are visible and
retry safely. - Consent and provider policy requirements are respected.

## Phase 6 --- Internal payroll

**Scope** - Employee records. - Salary configuration. - Bonuses,
overtime, deductions and advances. - Internal payslip records. - PDF
generation and history.

**Acceptance** - Gross and net amounts follow documented configurable
rules. - Generated PDFs match persisted payslip records. - Historical
payslips cannot be silently overwritten. - UI clearly identifies
statements as internal documents.

## Phase 7 --- Reports and exports

**Scope** - Sales reports. - Income and expense reports. - Cash flow. -
Receivables and payables. - Customer statements. - Product
performance. - Negotiation reports. - PDF, XLSX and CSV exports.

**Acceptance** - Filters are applied consistently. - Exported totals
reconcile with on-screen results. - Large exports are handled safely.

## Phase 8 --- Production readiness

**Scope** - Full regression tests. - Security review. - Performance
review. - Logging and monitoring. - Database backup and restore. -
Deployment configuration. - Operational documentation.

**Acceptance** - Critical workflows pass automated tests. - Backup
restoration has been tested. - Secrets are managed securely. - HTTPS and
access restrictions are configured. - Deployment and rollback procedures
are documented.

## Suggested MVP boundary

The first usable MVP should include: 1. Authentication and company
settings. 2. Customers. 3. Products and services. 4. Sales and payment
recording. 5. Accounts receivable and payable. 6. Income, expenses and
cash flow. 7. Basic dashboard.

Negotiation history, notifications, payroll and advanced reports follow
after the core financial workflow is stable.

## Engineering quality gates

For every phase: - Add or update Flyway migrations. - Add backend unit
and integration tests. - Add frontend tests for important workflows. -
Review authorization. - Verify financial invariants. - Update API and
user documentation. - Demonstrate the feature with persisted data.


---

## 9. Granular implementation checklist

The phase scopes and acceptance criteria above remain the source of truth.
Use the following smaller checkboxes to track implementation. Keep each
phase open until its applicable subtasks and acceptance criteria are met.

### Phase 1 --- Foundation

#### Repository and local environment
- [ ] Verify the repository root and required folders.
- [ ] Verify frontend and backend are separate projects.
- [ ] Verify Docker Compose configuration.
- [ ] Verify PostgreSQL container startup.
- [ ] Verify persistent database volume.
- [ ] Verify PostgreSQL health check.
- [ ] Document required environment variables.
- [ ] Verify `.env.example` contains placeholders only.
- [ ] Verify no secrets are tracked by Git.

#### Backend foundation
- [ ] Verify the Spring Boot application starts.
- [ ] Verify PostgreSQL connectivity.
- [ ] Configure Spring Web.
- [ ] Configure Spring Data JPA.
- [ ] Configure Bean Validation.
- [ ] Configure Spring Security.
- [ ] Configure Flyway.
- [ ] Create the initial migration.
- [ ] Verify migrations run against an empty database.
- [ ] Implement a consistent API error response.
- [ ] Implement global exception handling.
- [ ] Configure OpenAPI documentation.
- [ ] Verify the API documentation endpoint.

#### Authentication and company settings
- [ ] Define user fields and roles.
- [ ] Create user and role migrations.
- [ ] Implement secure password hashing.
- [ ] Implement login.
- [ ] Implement logout/token revocation.
- [ ] Implement access-token validation.
- [ ] Implement refresh-token handling.
- [ ] Protect backend endpoints by default.
- [ ] Implement role-based permission checks.
- [ ] Define company settings fields.
- [ ] Create the company settings migration.
- [ ] Implement company settings retrieval and update.
- [ ] Build the frontend login screen.
- [ ] Connect login and logout to the API.
- [ ] Add protected frontend routes.
- [ ] Test authentication and authorization.

#### Foundation acceptance checks
- [ ] Start the full stack from a fresh environment.
- [ ] Confirm database migrations execute.
- [ ] Confirm users can log in and log out.
- [ ] Confirm protected endpoints enforce permissions.
- [ ] Confirm setup instructions are complete.
- [ ] Confirm no secrets are committed.

### Phase 2 --- Customers, products and sales

#### Customers
- [ ] Define customer data fields.
- [ ] Create the customer migration.
- [ ] Implement customer creation.
- [ ] Implement customer details retrieval.
- [ ] Implement customer update.
- [ ] Implement customer deactivation.
- [ ] Implement customer search and filters.
- [ ] Add pagination and sorting.
- [ ] Build customer list and form screens.
- [ ] Add customer validation tests.

#### Products and services
- [ ] Define product fields.
- [ ] Define service fields.
- [ ] Define code/SKU and unit conventions.
- [ ] Create product/service migrations.
- [ ] Implement product CRUD.
- [ ] Implement service CRUD.
- [ ] Implement active/inactive status.
- [ ] Confirm whether stock tracking is in MVP scope.
- [ ] If approved, define inventory movement rules before implementation.
- [ ] Build product/service list and form screens.
- [ ] Add product/service validation tests.

#### Sales and receivables
- [ ] Define sale and sale-item data models.
- [ ] Create sale and sale-item migrations.
- [ ] Implement sale creation.
- [ ] Validate customer selection.
- [ ] Validate product/service selection.
- [ ] Validate quantities and unit prices.
- [ ] Calculate line totals in the backend.
- [ ] Calculate discounts and additional charges.
- [ ] Calculate the final sale total in the backend.
- [ ] Define payment terms.
- [ ] Define receivable and installment data models.
- [ ] Create receivable migrations.
- [ ] Generate receivables from a sale.
- [ ] Implement due dates and installment schedules.
- [ ] Build sales and receivables screens.
- [ ] Test sale-to-receivable creation.
- [ ] Test backend total calculations.

#### Partial payments
- [ ] Define payment data fields.
- [ ] Implement payment registration against a receivable.
- [ ] Implement partial payment allocation.
- [ ] Calculate the remaining receivable balance.
- [ ] Prevent or explicitly handle overpayments.
- [ ] Prevent duplicate payment requests with idempotency.
- [ ] Test one partial payment.
- [ ] Test multiple partial payments.
- [ ] Test duplicate payment submission.

### Phase 3 --- Financial management

#### Income, expenses and obligations
- [ ] Define income transaction fields.
- [ ] Define expense transaction fields.
- [ ] Define expense categories.
- [ ] Define accounts payable fields.
- [ ] Confirm how income links to receivables.
- [ ] Confirm how expenses link to payables.
- [ ] Create required Flyway migrations.
- [ ] Implement income recording.
- [ ] Implement expense recording.
- [ ] Implement payable creation and retrieval.
- [ ] Implement receivable retrieval and filtering.
- [ ] Define recurring transaction requirements.
- [ ] Implement recurring transactions only if approved.

#### Payment allocation and cash flow
- [ ] Define payment allocation rules.
- [ ] Implement payment allocation to receivables.
- [ ] Implement payment allocation to payables.
- [ ] Implement full settlement.
- [ ] Implement partial settlement.
- [ ] Define cancellation behavior.
- [ ] Define reversal behavior.
- [ ] Preserve original financial records during cancellation/reversal.
- [ ] Define cash-basis reporting rules.
- [ ] Define accrual-basis reporting rules.
- [ ] Implement cash-flow inflow calculations.
- [ ] Implement cash-flow outflow calculations.
- [ ] Implement cash-flow date filters.
- [ ] Implement basic financial dashboard metrics.
- [ ] Reconcile dashboard metrics with underlying transactions.

#### Phase acceptance checks
- [ ] Verify actual receipts and disbursements are reported separately.
- [ ] Verify cash and accrual views are clearly distinguished.
- [ ] Verify partial settlement.
- [ ] Verify cancellation and reversal preserve history.
- [ ] Verify dashboard totals reconcile with source records.

### Phase 4 --- Negotiations

- [ ] Define negotiation fields and statuses.
- [ ] Define the relationship between a negotiation and a customer.
- [ ] Define the relationship between a negotiation and a sale.
- [ ] Create the negotiation migration.
- [ ] Implement negotiation creation.
- [ ] Implement negotiation update.
- [ ] Store notes and interaction dates.
- [ ] Store responsible user and outcome.
- [ ] Store next follow-up date.
- [ ] Build chronological negotiation timeline.
- [ ] Implement attachment upload.
- [ ] Enforce attachment access controls.
- [ ] Add customer and status filters.
- [ ] Implement conversion of a negotiation into a sale.
- [ ] Preserve the original proposal after conversion.
- [ ] Prevent duplicate conversion.
- [ ] Test retrieval by customer and status.
- [ ] Test conversion and duplicate-conversion prevention.

### Phase 5 --- Notifications

#### Templates and providers
- [ ] Define notification channels and template fields.
- [ ] Create notification template persistence.
- [ ] Implement template creation and editing.
- [ ] Select an email provider.
- [ ] Configure provider credentials through secrets/environment configuration.
- [ ] Implement email sending.
- [ ] Send and verify a test email.
- [ ] Confirm WhatsApp Business Cloud API account prerequisites.
- [ ] Confirm approved WhatsApp templates.
- [ ] Confirm consent and policy requirements.
- [ ] Implement WhatsApp integration after prerequisites are satisfied.
- [ ] Send and verify a test WhatsApp message through a test service.

#### Scheduling and delivery tracking
- [ ] Define reminder timing rules.
- [ ] Implement scheduled reminder generation.
- [ ] Add persistent job storage/processing.
- [ ] Persist delivery attempts.
- [ ] Persist provider responses.
- [ ] Persist timestamps and failure reasons.
- [ ] Implement retry handling with backoff.
- [ ] Implement deduplication across scheduler runs and retries.
- [ ] Exclude paid receivables.
- [ ] Exclude cancelled receivables.
- [ ] Implement delivery-history UI.
- [ ] Implement consent/preferences and opt-out handling.
- [ ] Test safe retries after provider failures.
- [ ] Test that duplicate reminders are not sent.

### Phase 6 --- Internal payroll

- [ ] Confirm the required employee/recipient fields.
- [ ] Define employee/recipient records.
- [ ] Define salary configuration fields.
- [ ] Define bonus inputs.
- [ ] Define overtime inputs.
- [ ] Define deduction inputs.
- [ ] Define advance inputs.
- [ ] Document configurable calculation rules.
- [ ] Create payroll-related migrations.
- [ ] Implement internal payroll-period records.
- [ ] Implement internal payslip records.
- [ ] Implement gross and net calculations from approved rules.
- [ ] Generate payslip PDFs.
- [ ] Persist the data used to generate each PDF.
- [ ] Preserve prior payslips when corrections are made.
- [ ] Restrict payroll access by role.
- [ ] Test PDF values against persisted payslip records.
- [ ] Label the feature clearly as internal statements, not statutory payroll.

### Phase 7 --- Reports and exports

- [ ] Define report filters and date semantics.
- [ ] Implement sales report.
- [ ] Implement income report.
- [ ] Implement expense report.
- [ ] Implement cash-flow report.
- [ ] Implement receivables report.
- [ ] Implement payables report.
- [ ] Implement customer statements.
- [ ] Implement product performance report.
- [ ] Implement negotiation report.
- [ ] Implement PDF export.
- [ ] Implement XLSX export.
- [ ] Implement CSV export.
- [ ] Enforce authorization on report endpoints.
- [ ] Enforce authorization on generated files.
- [ ] Test report filters.
- [ ] Compare exported totals with on-screen totals.
- [ ] Test large export behavior.

### Phase 8 --- Production readiness

#### Verification and security
- [ ] Run backend unit tests.
- [ ] Run backend integration tests.
- [ ] Run frontend tests.
- [ ] Run end-to-end regression tests for critical workflows.
- [ ] Review authentication and authorization.
- [ ] Review object-level access controls.
- [ ] Review input and file-upload validation.
- [ ] Review dependency vulnerabilities.
- [ ] Review container image vulnerabilities.
- [ ] Confirm HTTPS configuration.
- [ ] Confirm secrets are managed outside source control.

#### Operations and release
- [ ] Configure application logging.
- [ ] Configure application monitoring.
- [ ] Configure critical-failure alerts.
- [ ] Configure automated database backups.
- [ ] Perform a database restore test.
- [ ] Prepare deployment configuration.
- [ ] Document deployment steps.
- [ ] Document rollback steps.
- [ ] Prepare operational documentation.
- [ ] Review production environment variables.
- [ ] Run the release checklist.
- [ ] Confirm critical workflows pass automated tests.
- [ ] Confirm backup restoration has been tested.
- [ ] Confirm deployment and rollback procedures are documented.
