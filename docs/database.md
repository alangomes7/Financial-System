# Database Design

## 1. Database choice

Use PostgreSQL as the primary database, with Flyway migrations and
Spring Data JPA/Hibernate.

Use: - `NUMERIC(19, 2)` or an explicitly justified precision for
monetary values. - Java `BigDecimal` for money. - `DATE` for date-only
business concepts such as due dates. - `TIMESTAMPTZ` for event
timestamps. - Foreign keys and unique constraints for relational
integrity.

Choose precision and rounding rules deliberately. Document rounding
behavior for discounts, taxes and allocations.

## 2. Core entities

  Entity                 Purpose
  ---------------------- -----------------------------------------------
  Company                Company identity and settings
  User                   Application users
  Role                   Roles
  Permission             Permission definitions
  UserRole               User-role assignments
  Customer               Customer profiles
  Supplier               Supplier profiles
  Product                Products and services
  ProductCategory        Product categories
  InventoryMovement      Stock changes
  Sale                   Sale header
  SaleItem               Sale line items
  AccountReceivable      Customer obligation
  AccountPayable         Business obligation
  Payment                Actual payment/receipt
  PaymentAllocation      Allocation of a payment to an obligation
  ExpenseCategory        Expense classification
  Employee               Employee profile
  Payslip                Internal employee payment statement
  Negotiation            Commercial negotiation
  NegotiationNote        Negotiation timeline entries
  Attachment             File metadata
  Notification           Notification event and delivery status
  NotificationTemplate   Message templates
  AuditLog               Sensitive action history
  RefreshToken           Revocable refresh-token records, if persisted

The exact schema may evolve during implementation. Keep migrations
backward-compatible where practical.

## 3. Relationship overview

``` text
Company
  ├── Users
  ├── Customers
  │     ├── Sales
  │     │    ├── SaleItems ── Products
  │     │    └── AccountsReceivable
  │     ├── Negotiations
  │     │    ├── NegotiationNotes
  │     │    └── Attachments
  │     └── Notifications
  ├── Products
  │     └── InventoryMovements
  ├── Suppliers
  │     └── AccountsPayable
  ├── Expenses
  ├── Employees
  │     └── Payslips
  └── Users ── Roles / Permissions

Payment
  └── PaymentAllocations ── AccountReceivable or AccountPayable
```

Payment modeling should explicitly distinguish incoming receipts from
outgoing disbursements, either with separate domain types/tables or a
carefully constrained shared payment model.

## 4. Important modeling rules

### Sales and receivables

A sale is the commercial record. A receivable is the amount due from a
customer. A payment is the actual money received. These are not
interchangeable.

A sale may create one or more receivables according to its payment
terms. A receivable may be settled by multiple partial payments.

### Payment allocation

Use a payment allocation record to connect a payment to one or more
obligations. Validate that allocations do not exceed the payment amount
or the remaining amount due, except through an explicitly designed
credit/refund workflow.

### Expenses and payables

An expense records the business cost and its accounting/business
classification. A payable represents an amount due. A
disbursement/payment records money actually paid. Document the
recognition policy and avoid double-counting.

### Cancellation and refunds

Do not physically delete posted financial records. Use status
transitions, cancellation records, reversals or refund records with
actor, timestamp and reason.

### Audit

Audit sensitive create/update/cancel/refund/payment actions. Store only
the personal data necessary for traceability and never store credentials
or tokens.

## 5. Suggested common columns

For applicable business tables: - `id` (UUID recommended). -
`company_id` (single-company association, not a tenant-isolation
feature). - `created_at`. - `created_by`. - `updated_at`. -
`updated_by`. - `status` where relevant. - `version` for optimistic
locking where concurrent updates are possible.

Do not add meaningless columns to every table; define fields according
to the entity's lifecycle.

## 6. Indexes and constraints

Consider indexes for: - Customer name and tax identifier. - Product
SKU. - Sale number and sale date. - Receivable due date and status. -
Payable due date and status. - Payment date and reference. - Negotiation
customer, status and follow-up date. - Notification status and scheduled
time. - Audit entity type, entity ID and timestamp.

Enforce unique constraints for business identifiers such as sale numbers
and product SKUs where required.

Use database constraints for non-negative quantities and amounts where
appropriate. Validate state transitions in the service layer as well.

## 7. Migrations

All schema changes must be delivered through Flyway migrations, for
example:

``` text
backend/src/main/resources/db/migration/
├── V1__create_company_and_users.sql
├── V2__create_customers_and_suppliers.sql
├── V3__create_products_and_inventory.sql
├── V4__create_sales_and_sale_items.sql
├── V5__create_financial_obligations_and_payments.sql
└── V6__create_negotiations_and_notifications.sql
```

Migration names are illustrative. Never edit an already-applied
production migration; create a new migration.

## 8. Backup and recovery

Define: - Backup frequency. - Retention period. - Encrypted off-site
storage. - Access restrictions. - Restore procedure. - Recovery Point
Objective (RPO). - Recovery Time Objective (RTO).

Test restoration regularly. A backup that has never been restored is not
a verified recovery plan.
