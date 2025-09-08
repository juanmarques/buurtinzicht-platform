import { getRequestConfig } from 'next-intl/server';
import { notFound } from 'next/navigation';

export const locales = ['nl', 'fr', 'en'] as const;
export const defaultLocale = 'nl' as const;

export default getRequestConfig(async ({ locale }) => {
  // Validate that the incoming `locale` parameter is valid
  if (!locales.includes(locale as any)) notFound();

  return {
    messages: (await import(`./messages/${locale}.json`)).default
  };
});

export async function getMessages({ locale }: { locale: string }) {
  try {
    return (await import(`./messages/${locale}.json`)).default;
  } catch (error) {
    return (await import(`./messages/${defaultLocale}.json`)).default;
  }
}