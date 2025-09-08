'use client';

import React from 'react';
import Link from 'next/link';
import { MapPin } from 'lucide-react';
import { useTranslations } from 'next-intl';

export function Footer() {
  const t = useTranslations('footer');
  return (
    <footer className="bg-neutral-900 text-neutral-300 mt-20">
      <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8 py-12">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-8">
          {/* Logo and description */}
          <div className="md:col-span-2">
            <div className="flex items-center space-x-2 mb-4">
              <div className="h-8 w-8 rounded-lg bg-primary-600 flex items-center justify-center">
                <MapPin className="h-5 w-5 text-white" />
              </div>
              <span className="text-xl font-bold text-white">
                Buurtinzicht
              </span>
            </div>
            <p className="text-neutral-400 mb-6 max-w-md">
              {t('description')}
            </p>
          </div>

          {/* Links */}
          <div>
            <h3 className="text-white font-semibold mb-4">{t('product.title')}</h3>
            <ul className="space-y-2">
              <li><Link href="/search" className="hover:text-white transition-colors">{t('product.search')}</Link></li>
              <li><Link href="/compare" className="hover:text-white transition-colors">{t('product.compare')}</Link></li>
              <li><Link href="/pricing" className="hover:text-white transition-colors">{t('product.pricing')}</Link></li>
            </ul>
          </div>

          <div>
            <h3 className="text-white font-semibold mb-4">{t('company.title')}</h3>
            <ul className="space-y-2">
              <li><Link href="/about" className="hover:text-white transition-colors">{t('company.about')}</Link></li>
              <li><Link href="/contact" className="hover:text-white transition-colors">{t('company.contact')}</Link></li>
              <li><Link href="/privacy" className="hover:text-white transition-colors">{t('company.privacy')}</Link></li>
            </ul>
          </div>
        </div>

        <div className="border-t border-neutral-800 mt-8 pt-8">
          <p className="text-sm text-neutral-400 text-center">
            {t('copyright')}
          </p>
        </div>
      </div>
    </footer>
  );
}