# API Guidelines

## 1. API conventions

Base path:

``` text
/api/v1
```

Use REST-style resources, JSON request/response bodies, consistent HTTP
status codes, pagination and server-side validation.

All protected endpoints require authentication and authorization. The
backend is the authority for business rules and monetary calculations.

## 2. Suggested endpoints

### Authentication

``` http
POST /api/v1/auth/login
POST /api/v1/auth/refresh
POST /api/v1/auth/logout
POST /api/v1/auth/forgot-password
POST /api/v1/auth/reset-password
```

### Company and users

``` http
GET  /api/v1/company
PUT  /api/v1/company
GET  /api/v1/users
POST /api/v1/users
PUT  /api/v1/users/{id}
```

### Customers

``` http
GET    /api/v1/customers
POST   /api/v1/customers
GET    /api/v1/customers/{id}
PUT    /api/v1/customers/{id}
DELETE /api/v1/customers/{id}
GET    /api/v1/customers/{id}/history
GET    /api/v1/customers/{id}/statement
```

For financial and historical records, deletion should normally mean
deactivation, not physical deletion.

### Products and inventory

``` http
GET  /api/v1/products
POST /api/v1/products
GET  /api/v1/products/{id}
PUT  /api/v1/products/{id}
GET  /api/v1/products/{id}/movements
POST /api/v1/products/{id}/movements
```

### Sales and payments

``` http
GET  /api/v1/sales
POST /api/v1/sales
GET  /api/v1/sales/{id}
POST /api/v1/sales/{id}/cancel
POST /api/v1/sales/{id}/payments
GET  /api/v1/sales/{id}/receivables
```

### Finance

``` http
GET  /api/v1/finance/dashboard
GET  /api/v1/finance/cash-flow
GET  /api/v1/finance/receivables
GET  /api/v1/finance/payables
POST /api/v1/finance/receivables
POST /api/v1/finance/payables
POST /api/v1/finance/expenses
GET  /api/v1/finance/expenses
```

### Negotiations

``` http
GET  /api/v1/negotiations
POST /api/v1/negotiations
GET  /api/v1/negotiations/{id}
PUT  /api/v1/negotiations/{id}
POST /api/v1/negotiations/{id}/notes
POST /api/v1/negotiations/{id}/attachments
POST /api/v1/negotiations/{id}/convert-to-sale
```

### Payroll

``` http
GET  /api/v1/payroll/employees
POST /api/v1/payroll/employees
PUT  /api/v1/payroll/employees/{id}
GET  /api/v1/payroll/payslips
POST /api/v1/payroll/payslips
GET  /api/v1/payroll/payslips/{id}
GET  /api/v1/payroll/payslips/{id}/pdf
```

### Notifications

``` http
GET  /api/v1/notifications
GET  /api/v1/notifications/history
POST /api/v1/notifications/send
POST /api/v1/notifications/{id}/retry
GET  /api/v1/notification-templates
PUT  /api/v1/notification-templates/{id}
```

### Reports

``` http
GET /api/v1/reports/sales
GET /api/v1/reports/income
GET /api/v1/reports/expenses
GET /api/v1/reports/cash-flow
GET /api/v1/reports/receivables
GET /api/v1/reports/payables
GET /api/v1/reports/negotiations
```

Use query parameters for filters, sorting and pagination. Define export
endpoints or an explicit `format` parameter for PDF, XLSX and CSV.

### Pagination, sorting, and filtering

Collection endpoints use zero-based `page` and `size` query parameters.
The default size is 20 and the maximum is 100. Sort by repeating the
`sort` parameter as `sort=property,direction`, where direction is `asc`
or `desc`; each endpoint must document its supported properties and
defaults. For example:

```http
GET /api/v1/accounts?page=0&size=20&sort=createdAt,desc&accountNumber=123
```

List responses use the same envelope:

```json
{
  "content": [],
  "page": 0,
  "size": 20,
  "totalElements": 0,
  "totalPages": 0
}
```

Filters are optional named query parameters, applied server-side. Ignore
blank filters and document each filter's matching behavior. Account
search matches `accountNumber` case-insensitively by substring.
The accounts collection supports sorting by `accountNumber`, `balance`,
or `createdAt` (default: `createdAt,desc`); unsupported sort properties
return `400 Bad Request`.

## 3. Response conventions

Use a consistent error structure, for example:

``` json
{
  "timestamp": "2026-01-01T12:00:00Z",
  "status": 400,
  "code": "VALIDATION_ERROR",
  "message": "The request contains invalid data.",
  "fieldErrors": {
    "amount": "Amount must be greater than zero."
  },
  "path": "/api/v1/payments"
}
```

Do not expose stack traces, SQL details, secrets or internal
implementation data in API errors.

For list endpoints, use a consistent pagination envelope:

``` json
{
  "content": [],
  "page": 0,
  "size": 20,
  "totalElements": 0,
  "totalPages": 0
}
```

## 4. Financial API rules

-   Accept decimal monetary values as JSON numbers only if precision
    handling is explicitly controlled; decimal strings are often safer
    across JavaScript clients.
-   Recalculate totals on the backend.
-   Validate currency and rounding rules.
-   Use database transactions for multi-record financial operations.
-   Use idempotency keys for payment creation and other retryable
    operations.
-   Never trust a frontend-provided balance or payment status.
-   Return the persisted result after a successful operation.

## 5. Documentation and testing

OpenAPI is generated with Springdoc. The specification is available at
`/v3/api-docs` and Swagger UI at `/swagger-ui.html`. Add integration tests
for authorization, validation, state transitions, partial payments,
duplicate submissions, refunds and cancellation workflows.

The endpoint list is a proposed contract. Final request and response
DTOs must be documented before frontend integration.
