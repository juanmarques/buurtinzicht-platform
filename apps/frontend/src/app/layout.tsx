import type { Metadata } from "next";
import { Inter } from "next/font/google";
import "./globals.css";
import { Providers } from "./providers";

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
  manifest: "/manifest.json",
  appleWebApp: {
    capable: true,
    statusBarStyle: "default",
    title: "Buurtinzicht",
    startupImage: [
      {
        media: "(device-width: 320px) and (device-height: 568px) and (-webkit-device-pixel-ratio: 2)",
        url: "/icons/apple-touch-startup-image-640x1136.png",
      },
      {
        media: "(device-width: 375px) and (device-height: 667px) and (-webkit-device-pixel-ratio: 2)",
        url: "/icons/apple-touch-startup-image-750x1334.png",
      },
      {
        media: "(device-width: 375px) and (device-height: 812px) and (-webkit-device-pixel-ratio: 3)",
        url: "/icons/apple-touch-startup-image-1125x2436.png",
      },
      {
        media: "(device-width: 414px) and (device-height: 896px) and (-webkit-device-pixel-ratio: 2)",
        url: "/icons/apple-touch-startup-image-828x1792.png",
      },
    ],
  },
  icons: {
    icon: [
      { url: "/icons/icon-32x32.png", sizes: "32x32", type: "image/png" },
      { url: "/icons/icon-16x16.png", sizes: "16x16", type: "image/png" },
    ],
    shortcut: ["/favicon.ico"],
    apple: [
      { url: "/icons/apple-touch-icon.png", sizes: "180x180" },
      { url: "/icons/apple-touch-icon-152x152.png", sizes: "152x152" },
      { url: "/icons/apple-touch-icon-144x144.png", sizes: "144x144" },
      { url: "/icons/apple-touch-icon-120x120.png", sizes: "120x120" },
      { url: "/icons/apple-touch-icon-114x114.png", sizes: "114x114" },
      { url: "/icons/apple-touch-icon-76x76.png", sizes: "76x76" },
      { url: "/icons/apple-touch-icon-72x72.png", sizes: "72x72" },
      { url: "/icons/apple-touch-icon-60x60.png", sizes: "60x60" },
      { url: "/icons/apple-touch-icon-57x57.png", sizes: "57x57" },
    ],
  },
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
  other: {
    "mobile-web-app-capable": "yes",
    "apple-mobile-web-app-capable": "yes",
    "apple-mobile-web-app-status-bar-style": "default",
    "msapplication-TileColor": "#2563eb",
    "msapplication-config": "/browserconfig.xml",
  },
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html className={inter.variable}>
      <body className="min-h-screen bg-neutral-50 text-neutral-900 font-sans antialiased">
        <Providers>
          {children}
        </Providers>
      </body>
    </html>
  );
}