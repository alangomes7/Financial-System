package br.com.financial.shared.health;

import java.time.Instant;

/**
 * Body returned by {@code GET /api/v1/health}.
 *
 * @param status overall application status
 * @param database PostgreSQL connectivity status
 * @param timestamp time the check was performed (UTC instant)
 */
public record HealthResponse(HealthStatus status, HealthStatus database, Instant timestamp) {}
