# Frontend — Next.js

Next.js (App Router) + React + TypeScript + Tailwind CSS.

## Requirements

- Node.js 24 LTS (see `.nvmrc`) and npm

## Setup

```bash
cd frontend
npm ci                      # installs exact versions from package-lock.json
cp .env.example .env.local  # Windows: Copy-Item .env.example .env.local
```

`NEXT_PUBLIC_API_URL` defaults to `http://localhost:8080/api/v1` if unset.
Variables prefixed with `NEXT_PUBLIC_` are embedded in the browser bundle —
never put secrets in them.

## Scripts

| Command             | Description                                      |
| ------------------- | ------------------------------------------------ |
| `npm run dev`       | Development server at http://localhost:3000      |
| `npm run build`     | Production build                                 |
| `npm start`         | Serve the production build                       |
| `npm run lint`      | ESLint                                           |
| `npm run typecheck` | Generate Next.js route types and run `tsc`       |
| `npm test`          | Unit tests (Vitest)                              |

## Structure

```text
src/
├── app/          # Routes (App Router)
├── components/   # Reusable UI components
└── lib/          # API client and framework-agnostic helpers (+ *.test.ts)
```

The home page calls `GET /api/v1/health` from the browser to show whether the
backend and database are reachable. The backend must allow the frontend
origin through `APP_CORS_ALLOWED_ORIGINS` (default `http://localhost:3000`).
