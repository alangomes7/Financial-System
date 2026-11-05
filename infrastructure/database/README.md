# Database support files

| Path    | Purpose                                                                 |
| ------- | ----------------------------------------------------------------------- |
| `init/` | Scripts mounted into `/docker-entrypoint-initdb.d` by `docker-compose.yml`. |

## Rules

- Scripts in `init/` run **only once**, when the `financial-system-postgres-data`
  volume is created. Changing them has no effect on an existing volume.
- `init/` must contain only database-level settings. **Tables, indexes and
  other schema objects belong in Flyway migrations** inside the backend
  (`backend/src/main/resources/db/migration`, introduced in Phase 2).
- Never place real data, credentials or dumps in this directory.

## Resetting the local database

This permanently deletes all local data:

```bash
docker compose down -v
docker compose up -d postgres
```

