import { BackendStatus } from "@/components/backend-status";

export default function Home() {
  return (
    <main className="mx-auto flex w-full max-w-3xl flex-1 flex-col gap-8 px-6 py-24">
      <header className="flex flex-col gap-2">
        <p className="text-sm font-medium uppercase tracking-wide text-zinc-500">Fase 1 · Ambiente local</p>
        <h1 className="text-3xl font-semibold tracking-tight">Sistema de Gestão Financeira</h1>
        <p className="text-zinc-600 dark:text-zinc-400">
          Estrutura inicial do frontend. Os módulos de negócio serão adicionados nas próximas fases.
        </p>
      </header>

      <BackendStatus />
    </main>
  );
}
