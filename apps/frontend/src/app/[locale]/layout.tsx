import React from 'react';
import { Inter } from 'next/font/google';
import { NextIntlClientProvider } from 'next-intl';
import { getMessages } from '../../../i18n';
import { Header } from '../../components/layout/Header';
import { Footer } from '../../components/layout/Footer';
import { PWAInstaller } from '../../components/pwa/PWAInstaller';

const inter = Inter({ subsets: ['latin'] });

export const metadata = {
  title: 'Buurtinzicht',
  description: 'Neighborhood Insights System',
};

export default async function LocaleLayout({
  children,
  params
}: {
  children: React.ReactNode;
  params: Promise<{ locale: string }>;
}) {
  const { locale } = await params; // Await params in Next.js 15
  // Providing all messages to the client
  // side is the easiest way to get started
  const messages = await getMessages({ locale }); // Pass locale to getMessages

  return (
    <html lang={locale}>
      <body className={inter.className}>
        <NextIntlClientProvider messages={messages}>
          <div className="flex flex-col min-h-screen">
            <Header />
            <main className="flex-1">
              {children}
            </main>
            <Footer />
            <PWAInstaller />
          </div>
        </NextIntlClientProvider>
      </body>
    </html>
  );
}