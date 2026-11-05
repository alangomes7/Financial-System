-- Runs once, only when the PostgreSQL data volume is first initialized.
-- Schema objects are NOT created here: all schema changes are managed by
-- Flyway migrations in the backend (introduced in Phase 2).

-- Ensure the application database uses the business time zone by default.
DO $$
BEGIN
    EXECUTE format('ALTER DATABASE %I SET timezone TO %L', current_database(), 'America/Sao_Paulo');
END
$$;

