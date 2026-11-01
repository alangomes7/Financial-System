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
