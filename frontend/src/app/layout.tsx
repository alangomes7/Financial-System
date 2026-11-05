import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "Sistema de Gestão Financeira",
  description: "Gestão financeira para uma empresa: vendas, recebíveis, despesas e fluxo de caixa.",
};

export default function RootLayout({ children }: LayoutProps<"/">) {
  return (
    <html lang="pt-BR" className="h-full antialiased">
      <body className="min-h-full flex flex-col">{children}</body>
    </html>
  );
}
