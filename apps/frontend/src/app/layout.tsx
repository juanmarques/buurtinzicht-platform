import type { Metadata } from "next";
import { Inter } from "next/font/google";
import "./globals.css";
import { Providers } from "./providers";
import { Header } from "../components/layout/Header";
import { Footer } from "../components/layout/Footer";

const inter = Inter({
  subsets: ["latin"],
  display: "swap",
  variable: "--font-inter",
});

export const metadata: Metadata = {
  title: {
    template: "%s | Buurtinzicht",
    default: "Buurtinzicht - Ontdek je ideale buurt in België",
  },
  description: "Ontdek en vergelijk Belgische buurten met gedetailleerde analyses en inzichten voor slimmere woonkeuzes. Van veiligheid tot voorzieningen - alles wat je nodig hebt om je volgende thuis te vinden.",
  keywords: [
    "België", "buurten", "wonen", "immobiliën", "veiligheid", "voorzieningen", 
    "transport", "scholen", "neighborhood", "Belgium", "real estate"
  ],
  authors: [{ name: "Buurtinzicht Team" }],
  creator: "Buurtinzicht",
  openGraph: {
    title: "Buurtinzicht - Ontdek je ideale buurt in België",
    description: "Ontdek en vergelijk Belgische buurten met gedetailleerde analyses en inzichten.",
    url: "https://buurtinzicht.be",
    siteName: "Buurtinzicht",
    locale: "nl_BE",
    type: "website",
  },
  twitter: {
    card: "summary_large_image",
    title: "Buurtinzicht - Ontdek je ideale buurt in België",
    description: "Ontdek en vergelijk Belgische buurten met gedetailleerde analyses en inzichten.",
    creator: "@buurtinzicht",
  },
  robots: {
    index: true,
    follow: true,
    googleBot: {
      index: true,
      follow: true,
      "max-video-preview": -1,
      "max-image-preview": "large",
      "max-snippet": -1,
    },
  },
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="nl" className={inter.variable}>
      <body className="min-h-screen bg-neutral-50 text-neutral-900 font-sans antialiased">
        <Providers>
          <div className="flex flex-col min-h-screen">
            <Header />
            <main className="flex-1">
              {children}
            </main>
            <Footer />
          </div>
        </Providers>
      </body>
    </html>
  );
}