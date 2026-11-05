import { describe, expect, it, vi } from "vitest";

import { fetchBackendHealth, getApiBaseUrl, parseBackendHealth } from "./api";

function jsonResponse(body: unknown, status = 200): Response {
  return new Response(JSON.stringify(body), {
    status,
    headers: { "Content-Type": "application/json" },
  });
}

describe("getApiBaseUrl", () => {
  it("falls back to the local default", () => {
    expect(getApiBaseUrl(undefined)).toBe("http://localhost:8080/api/v1");
    expect(getApiBaseUrl("  ")).toBe("http://localhost:8080/api/v1");
  });

  it("removes trailing slashes", () => {
    expect(getApiBaseUrl("https://api.example.com/api/v1//")).toBe("https://api.example.com/api/v1");
  });
});

describe("parseBackendHealth", () => {
  it("accepts a valid body", () => {
    const body = { status: "UP", database: "UP", timestamp: "2026-11-01T15:00:00Z" };
    expect(parseBackendHealth(body)).toEqual(body);
  });

  it.each([null, "UP", { status: "OK", database: "UP", timestamp: "x" }, { status: "UP", database: "UP" }])(
    "rejects an invalid body: %j",
    (body) => {
      expect(() => parseBackendHealth(body)).toThrow();
    },
  );
});

describe("fetchBackendHealth", () => {
  it("calls the health endpoint and parses the response", async () => {
    const fetchFn = vi.fn().mockResolvedValue(
      jsonResponse({ status: "UP", database: "UP", timestamp: "2026-11-01T15:00:00Z" }),
    );

    const health = await fetchBackendHealth(fetchFn, "http://backend/api/v1");

    expect(fetchFn).toHaveBeenCalledWith("http://backend/api/v1/health", expect.any(Object));
    expect(health.status).toBe("UP");
  });

  it("parses a 503 response reporting the database as down", async () => {
    const fetchFn = vi.fn().mockResolvedValue(
      jsonResponse({ status: "DOWN", database: "DOWN", timestamp: "2026-11-01T15:00:00Z" }, 503),
    );

    const health = await fetchBackendHealth(fetchFn, "http://backend/api/v1");

    expect(health.database).toBe("DOWN");
  });

  it("throws on other HTTP errors", async () => {
    const fetchFn = vi.fn().mockResolvedValue(new Response("", { status: 500 }));

    await expect(fetchBackendHealth(fetchFn, "http://backend/api/v1")).rejects.toThrow("HTTP 500");
  });
});

