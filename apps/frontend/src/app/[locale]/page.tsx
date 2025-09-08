'use client';

import React from 'react';
import Link from 'next/link';
import { Search, MapPin, BarChart3, Shield, Clock, Users, ArrowRight } from 'lucide-react';
import { Button } from '../../components/ui/Button';
import { useTranslations } from 'next-intl';

export default function Home() {
  const t = useTranslations('homepage');
  const tCommon = useTranslations('common');

  const features = [
    {
      icon: MapPin,
      title: t('features.search.title'),
      description: t('features.search.description'),
    },
    {
      icon: BarChart3,
      title: t('features.scorecards.title'),
      description: t('features.scorecards.description'),
    },
    {
      icon: Shield,
      title: t('features.safety.title'),
      description: t('features.safety.description'),
    },
    {
      icon: Clock,
      title: t('features.realtime.title'),
      description: t('features.realtime.description'),
    },
    {
      icon: Users,
      title: t('features.compare.title'),
      description: t('features.compare.description'),
    },
  ];

  return (
    <div className="min-h-screen bg-white">
      {/* Hero Section */}
      <section className="relative py-20 px-4 sm:px-6 lg:px-8">
        <div className="mx-auto max-w-7xl">
          <div className="text-center">
            <h1 className="text-4xl sm:text-5xl lg:text-6xl font-bold text-neutral-900 mb-6 leading-tight">
              {t.rich('title', {
                span: (chunks) => (
                  <span className="text-transparent bg-clip-text bg-gradient-to-r from-blue-600 to-blue-800">
                    {chunks}
                  </span>
                ),
              })}
            </h1>
            <p className="text-lg sm:text-xl text-neutral-700 mb-10 max-w-3xl mx-auto leading-relaxed">
              {t('subtitle')}
            </p>
            
            {/* Interactive Search Bar */}
            <div className="mb-12 max-w-2xl mx-auto">
              <div className="relative">
                <input
                  type="text"
                  placeholder={t('searchPlaceholder')}
                  className="w-full px-6 py-4 text-lg border-2 border-neutral-200 rounded-2xl focus:border-blue-500 focus:outline-none focus:ring-4 focus:ring-blue-100 transition-all shadow-sm"
                />
                <Button 
                  size="lg" 
                  className="absolute right-2 top-2 bg-blue-600 hover:bg-blue-700 rounded-xl"
                  icon={Search}
                >
                  {t('searchButton')}
                </Button>
              </div>
              <p className="text-sm text-neutral-500 mt-3">
                {t.rich('searchExample', {
                  span: (chunks) => <span className="font-medium">{chunks}</span>,
                })}
              </p>
            </div>
            
            {/* Dual-Path User Segmentation */}
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-6 max-w-3xl mx-auto">
              <Link href="/search?type=family" className="group">
                <div className="p-8 border-2 border-neutral-200 rounded-2xl hover:border-blue-300 hover:bg-blue-50 transition-all cursor-pointer h-full">
                  <div className="text-blue-600 mb-4">
                    <Users className="h-12 w-12 mx-auto" />
                  </div>
                  <h3 className="font-semibold text-xl text-neutral-900 mb-3 group-hover:text-blue-700">
                    {t('forFamily')}
                  </h3>
                  <p className="text-neutral-600 mb-4">
                    {t('familyDescription')}
                  </p>
                  <div className="flex items-center justify-center text-blue-600 group-hover:text-blue-700 font-medium">
                    {tCommon('startSearch')} <ArrowRight className="h-4 w-4 ml-2 group-hover:translate-x-1 transition-transform" />
                  </div>
                </div>
              </Link>
              
              <Link href="/search?type=business" className="group">
                <div className="p-8 border-2 border-neutral-200 rounded-2xl hover:border-green-300 hover:bg-green-50 transition-all cursor-pointer h-full">
                  <div className="text-green-600 mb-4">
                    <BarChart3 className="h-12 w-12 mx-auto" />
                  </div>
                  <h3 className="font-semibold text-xl text-neutral-900 mb-3 group-hover:text-green-700">
                    {t('forBusiness')}
                  </h3>
                  <p className="text-neutral-600 mb-4">
                    {t('businessDescription')}
                  </p>
                  <div className="flex items-center justify-center text-green-600 group-hover:text-green-700 font-medium">
                    {tCommon('viewOpportunities')} <ArrowRight className="h-4 w-4 ml-2 group-hover:translate-x-1 transition-transform" />
                  </div>
                </div>
              </Link>
            </div>
          </div>
        </div>
      </section>

      {/* Features Section */}
      <section className="py-20 px-4 sm:px-6 lg:px-8 bg-neutral-50">
        <div className="mx-auto max-w-7xl">
          <div className="text-center mb-16">
            <h2 className="text-3xl sm:text-4xl font-bold text-neutral-900 mb-6">
              {t('whyChoose')}
            </h2>
            <p className="text-lg text-neutral-600 max-w-2xl mx-auto">
              {t('whyDescription')}
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
            {features.map((feature, index) => {
              const Icon = feature.icon;
              return (
                <div key={index} className="bg-white p-8 rounded-2xl shadow-sm border border-neutral-100 hover:shadow-md transition-all">
                  <div className="inline-flex items-center justify-center w-14 h-14 rounded-xl bg-blue-100 mb-6">
                    <Icon className="h-7 w-7 text-blue-600" />
                  </div>
                  <h3 className="text-xl font-semibold text-neutral-900 mb-4">
                    {feature.title}
                  </h3>
                  <p className="text-neutral-600 leading-relaxed">
                    {feature.description}
                  </p>
                </div>
              );
            })}
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="py-20 px-4 sm:px-6 lg:px-8 bg-white">
        <div className="mx-auto max-w-4xl text-center">
          <h2 className="text-3xl sm:text-4xl font-bold text-neutral-900 mb-6">
            {t('cta.title')}
          </h2>
          <p className="text-lg text-neutral-600 mb-10 max-w-2xl mx-auto">
            {t('cta.subtitle')}
          </p>
          <div className="flex flex-col sm:flex-row gap-4 justify-center">
            <Link href="/search">
              <Button size="lg" icon={Search} className="bg-blue-600 hover:bg-blue-700 text-lg px-8 py-4 rounded-xl">
                {tCommon('startFreeSearch')}
              </Button>
            </Link>
            <Link href="/pricing">
              <Button variant="outline" size="lg" className="text-lg px-8 py-4 rounded-xl border-2">
                {tCommon('viewPricing')}
              </Button>
            </Link>
          </div>
        </div>
      </section>
    </div>
  );
}