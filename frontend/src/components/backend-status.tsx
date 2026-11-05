"use client";

import { useEffect, useState } from "react";

import { type BackendHealth, fetchBackendHealth, getApiBaseUrl } from "@/lib/api";

type State =
  | { kind: "loading" }
  | { kind: "ok"; health: BackendHealth }
  | { kind: "error"; message: string };

const STATUS_LABEL = { UP: "Disponível", DOWN: "Indisponível" } as const;

export function BackendStatus() {
  const [state, setState] = useState<State>({ kind: "loading" });

  useEffect(() => {
    const controller = new AbortController();
    fetchBackendHealth(fetch, getApiBaseUrl(), controller.signal)
      .then((health) => setState({ kind: "ok", health }))
      .catch((error: unknown) => {
        if (controller.signal.aborted) return;
        setState({ kind: "error", message: error instanceof Error ? error.message : "Erro desconhecido" });
      });
    return () => controller.abort();
  }, []);

  return (
    <section
      aria-live="polite"
      className="rounded-lg border border-zinc-200 p-6 dark:border-zinc-800"
    >
      <h2 className="mb-4 text-lg font-medium">Status do backend</h2>
      {state.kind === "loading" && <p className="text-zinc-500">Verificando…</p>}
      {state.kind === "error" && (
        <p className="text-red-600 dark:text-red-400">
          Não foi possível conectar ao backend em <code>{getApiBaseUrl()}</code>. {state.message}
        </p>
      )}
      {state.kind === "ok" && (
        <dl className="grid grid-cols-[max-content_1fr] gap-x-6 gap-y-2">
          <dt className="text-zinc-500">Aplicação</dt>
          <dd>{STATUS_LABEL[state.health.status]}</dd>
          <dt className="text-zinc-500">Banco de dados</dt>
          <dd>{STATUS_LABEL[state.health.database]}</dd>
        </dl>
      )}
    </section>
  );
}

