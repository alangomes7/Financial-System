export type HealthStatus = "UP" | "DOWN";

export interface BackendHealth {
  status: HealthStatus;
  database: HealthStatus;
  timestamp: string;
}

const DEFAULT_API_URL = "http://localhost:8080/api/v1";

/** Base URL of the versioned backend API, without a trailing slash. */
export function getApiBaseUrl(value: string | undefined = process.env.NEXT_PUBLIC_API_URL): string {
  const url = value?.trim() || DEFAULT_API_URL;
  return url.replace(/\/+$/, "");
}

function isHealthStatus(value: unknown): value is HealthStatus {
  return value === "UP" || value === "DOWN";
}

/** Validates the shape of a `GET /health` response body. */
export function parseBackendHealth(body: unknown): BackendHealth {
  if (typeof body !== "object" || body === null) {
    throw new Error("Resposta de saúde inválida");
  }
  const { status, database, timestamp } = body as Record<string, unknown>;
  if (!isHealthStatus(status) || !isHealthStatus(database) || typeof timestamp !== "string") {
    throw new Error("Resposta de saúde inválida");
  }
  return { status, database, timestamp };
}

/**
 * Calls the backend health endpoint. A 503 response still carries a valid body
 * (database DOWN), so it is parsed rather than treated as a network failure.
 */
export async function fetchBackendHealth(
  fetchFn: typeof fetch = fetch,
  baseUrl: string = getApiBaseUrl(),
  signal?: AbortSignal,
): Promise<BackendHealth> {
  const response = await fetchFn(`${baseUrl}/health`, {
    headers: { Accept: "application/json" },
    cache: "no-store",
    signal,
  });
  if (!response.ok && response.status !== 503) {
    throw new Error(`Falha ao consultar o backend (HTTP ${response.status})`);
  }
  return parseBackendHealth(await response.json());
}

