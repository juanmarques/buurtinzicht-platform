'use client';

import React from 'react';
import Link from 'next/link';
import { MapPin } from 'lucide-react';

export function Footer() {
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
              Ontdek en vergelijk Belgische buurten met gedetailleerde analyses 
              en inzichten voor slimmere woonkeuzes.
            </p>
          </div>

          {/* Links */}
          <div>
            <h3 className="text-white font-semibold mb-4">Product</h3>
            <ul className="space-y-2">
              <li><Link href="/search" className="hover:text-white transition-colors">Buurt Zoeken</Link></li>
              <li><Link href="/compare" className="hover:text-white transition-colors">Vergelijken</Link></li>
              <li><Link href="/pricing" className="hover:text-white transition-colors">Prijzen</Link></li>
            </ul>
          </div>

          <div>
            <h3 className="text-white font-semibold mb-4">Bedrijf</h3>
            <ul className="space-y-2">
              <li><Link href="/about" className="hover:text-white transition-colors">Over Ons</Link></li>
              <li><Link href="/contact" className="hover:text-white transition-colors">Contact</Link></li>
              <li><Link href="/privacy" className="hover:text-white transition-colors">Privacy</Link></li>
            </ul>
          </div>
        </div>

        <div className="border-t border-neutral-800 mt-8 pt-8">
          <p className="text-sm text-neutral-400 text-center">
            © 2024 Buurtinzicht. Alle rechten voorbehouden.
          </p>
        </div>
      </div>
    </footer>
  );
}