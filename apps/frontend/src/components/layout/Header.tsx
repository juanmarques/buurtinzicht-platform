'use client';

import React from 'react';
import Link from 'next/link';
import { Menu, X, MapPin, Search, User, Heart } from 'lucide-react';
import { Button } from '../ui/Button';

export function Header() {
  const [mobileMenuOpen, setMobileMenuOpen] = React.useState(false);

  const navigation = [
    { name: 'Buurt Zoeken', href: '/search' },
    { name: 'Mijn Favorieten', href: '/favorites' },
    { name: 'API Docs', href: '/docs' },
    { name: 'Prijzen', href: '/pricing' },
  ];

  return (
    <header className="bg-white border-b border-neutral-200 sticky top-0 z-50">
      <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between items-center h-16">
          {/* Logo */}
          <Link href="/" className="flex items-center space-x-2">
            <div className="h-8 w-8 rounded-lg bg-primary-600 flex items-center justify-center">
              <MapPin className="h-5 w-5 text-white" />
            </div>
            <span className="text-xl font-bold text-neutral-900">
              Buurtinzicht
            </span>
          </Link>
          
          {/* Desktop navigation */}
          <nav className="hidden md:flex md:space-x-8">
            {navigation.map((item) => (
              <Link
                key={item.name}
                href={item.href}
                className="text-neutral-600 hover:text-primary-600 font-medium transition-colors"
              >
                {item.name}
              </Link>
            ))}
          </nav>

          {/* CTA Buttons */}
          <div className="hidden md:flex items-center space-x-4">
            <Link href="/favorites">
              <Button variant="ghost" size="sm" icon={Heart}>
                Favorieten
              </Button>
            </Link>
            <Link href="/dashboard">
              <Button variant="outline" size="sm" icon={User}>
                Dashboard
              </Button>
            </Link>
            <Link href="/search">
              <Button variant="primary" size="sm" icon={Search}>
                Zoeken
              </Button>
            </Link>
          </div>

          {/* Mobile menu button */}
          <Button
            variant="ghost"
            size="sm"
            className="md:hidden"
            onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
          >
            {mobileMenuOpen ? (
              <X className="h-6 w-6" />
            ) : (
              <Menu className="h-6 w-6" />
            )}
          </Button>
        </div>
      </div>

      {/* Mobile menu */}
      {mobileMenuOpen && (
        <div className="md:hidden border-t border-neutral-200 bg-white">
          <div className="px-4 py-2 space-y-1">
            {navigation.map((item) => (
              <Link
                key={item.name}
                href={item.href}
                className="block px-3 py-2 text-neutral-600 hover:text-primary-600 hover:bg-neutral-50 rounded-lg font-medium"
                onClick={() => setMobileMenuOpen(false)}
              >
                {item.name}
              </Link>
            ))}
            <div className="pt-2 space-y-2">
              <Link href="/dashboard" className="block">
                <Button variant="outline" size="sm" className="w-full" icon={User}>
                  Dashboard
                </Button>
              </Link>
              <Link href="/search" className="block">
                <Button variant="primary" size="sm" className="w-full" icon={Search}>
                  Begin met zoeken
                </Button>
              </Link>
            </div>
          </div>
        </div>
      )}
    </header>
  );
}