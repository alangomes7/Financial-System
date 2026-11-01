# Product Requirements

## 1. Product scope

Build a financial and commercial management application for one
Brazilian company. The initial user interface is in Brazilian Portuguese
(`pt-BR`), currency is BRL, date format is `DD/MM/YYYY`, and timezone is
`America/Sao_Paulo`.

### In scope

-   Dashboard.
-   Customer management.
-   Products and services.
-   Sales and payment recording.
-   Income and expense management.
-   Accounts receivable and payable.
-   Cash-flow reporting.
-   Negotiation history and follow-ups.
-   Internal employee payment statements.
-   Email and WhatsApp payment reminders.
-   PDF, XLSX and CSV reports.
-   User authentication, roles and audit logs.

### Out of scope for the initial release

-   Multi-tenant SaaS operation.
-   Subscription billing.
-   Official CLT payroll compliance.
-   eSocial integration.
-   Automated statutory tax calculations.
-   Electronic invoicing and tax-document issuance.
-   Bank aggregation and automatic reconciliation.
-   Native mobile applications.

## 2. Roles

Initial roles: - **Administrator:** full system configuration and
access. - **Financial Manager:** finance, payments, reports and
permitted customer information. - **Sales Manager:** customers,
products, sales and negotiations. - **Employee:** only the features
explicitly granted to that user.

Implement permissions as backend-enforced capabilities. Role names alone
must not be treated as sufficient authorization for sensitive records.

## 3. Functional requirements

### FR-01 Dashboard

Display total income, total expenses, net result, sales, receivables,
payables, overdue balances, upcoming due dates and recent activity.
Provide date filters for today, week, month, previous month, year and
custom range.

Dashboard values must be calculated from persisted backend data and
clearly distinguish received/paid cash from accrual figures.

### FR-02 Customers

Create, view, edit, search and deactivate customer records. Store
name/company name, CPF/CNPJ where applicable, email, telephone,
WhatsApp, address, registration date, status and notes.

Show customer sales, payments, outstanding balances, negotiations and a
chronological history.

### FR-03 Products and services

Manage products and services with name, code/SKU, description, category,
cost, sale price, status and applicable stock settings.

For physical products, support stock quantity, minimum stock and stock
movement history. Services should not require inventory quantities.

### FR-04 Sales

Create sales for a customer with one or more line items. Store quantity,
unit price, discount and calculated totals. Support payment terms,
multiple installments and partial payments.

Sale states include draft, pending, partially paid, paid, overdue,
cancelled and refunded. State transitions must be validated by the
backend.

### FR-05 Receivables and payments

Create accounts receivable from sales or permitted manual entries. Track
original amount, due date, outstanding amount, status and linked
customer.

Record payments independently, including amount, date, method, reference
and allocation. Support partial payments and prevent overpayment unless
an explicit credit/refund workflow is defined.

Payment methods may include cash, PIX, bank transfer, credit card, debit
card, boleto and other.

### FR-06 Payables and expenses

Register suppliers, expense categories, one-time and recurring expenses,
installments, due dates, payment dates, methods, attachments and notes.

Distinguish a payable obligation from the actual disbursement. Support
partial settlement where appropriate.

### FR-07 Cash flow and financial reports

Report actual receipts and disbursements by date and category. Provide
separate accrual-oriented views where the underlying recognition rules
are defined.

Calculate net cash movement and operating results using documented
formulas. Do not label cash movement as profit.

### FR-08 Negotiations

Create a negotiation linked to a customer. Record subject, date,
products/services, proposed prices, discounts, payment terms, notes,
attachments, status and next follow-up date.

Statuses: new, in progress, waiting for customer, proposal sent,
approved, rejected and converted into sale.

Allow users to search and resume historical negotiations. Conversion
into a sale must preserve the original negotiation and link both
records.

### FR-09 Internal payroll

Maintain employee records and internal monthly payment statements.
Support configured salary, bonuses, overtime, deductions, advances,
gross amount, net amount, payment date and status.

Generate downloadable and printable PDF statements and preserve history.

**Legal boundary:** These are internal payment statements only. Do not
claim official Brazilian payroll compliance or calculate statutory INSS,
IRRF, FGTS or eSocial obligations without validated, current rules and
qualified review.

### FR-10 Notifications

Send payment reminders by email and official WhatsApp Business Platform
Cloud API.

Default configurable schedule: - 3 days before due date. - On due
date. - 3 days overdue. - 7 days overdue.

Allow manual reminders, templates, notification preferences, consent
records, delivery history, provider IDs and failure inspection.

Do not send reminders for paid or cancelled receivables. Prevent
duplicate reminders for the same receivable, channel, schedule event and
reminder cycle.

WhatsApp templates and messaging must follow current Meta platform
requirements. Provider charges may apply.

### FR-11 Reports and exports

Provide sales, income, expense, cash-flow, receivables, payables,
overdue account, customer statement, product performance, payslip and
negotiation reports.

Support PDF, XLSX and CSV export with applicable filters.

### FR-12 Audit

Record actor, timestamp, action, affected record, and relevant
before/after values for sensitive operations. Do not log passwords,
tokens or unnecessary personal data.

## 4. Non-functional requirements

### NFR-01 Usability

Responsive interface, clear Brazilian Portuguese labels, accessible
forms, consistent validation, useful empty states and understandable
error messages.

### NFR-02 Data integrity

Use database constraints and transactions. Financial amounts must use
exact decimal representations.

### NFR-03 Security

Authentication, authorization, password hashing, secure session/token
handling, rate limiting, secure uploads, secret management and audit
logging.

### NFR-04 Reliability

Back up the database, document restoration and test recovery. Background
jobs must be retryable and idempotent.

### NFR-05 Maintainability

Modular backend packages, feature-oriented frontend structure,
documented APIs, migrations and automated tests.

### NFR-06 Localization

Use `pt-BR`, `BRL`, `DD/MM/YYYY` and `America/Sao_Paulo`. Store
timestamps consistently (prefer UTC in the database) and convert for
display.

## 5. Acceptance criteria

A feature is accepted only when: 1. The UI workflow is functional. 2.
Backend validation and authorization are enforced. 3. Data persists
correctly. 4. Error and edge cases are handled. 5. Automated tests cover
core behavior. 6. Documentation is updated. 7. External integrations are
verified in a sandbox or test environment.

No mock integration may be described as production-ready.
