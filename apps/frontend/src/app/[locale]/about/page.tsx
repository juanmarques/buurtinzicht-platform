'use client';

import React from 'react';
import Link from 'next/link';
import { 
  MapPin, 
  Users, 
  Target, 
  Award,
  Heart,
  Zap,
  Shield,
  Globe
} from 'lucide-react';
import { Button } from '../../components/ui/Button';
import { useTranslations } from 'next-intl';

export default function AboutPage() {
  const t = useTranslations('about');

  const values = [
    {
      icon: Target,
      title: t('values.precision.title'),
      description: t('values.precision.description')
    },
    {
      icon: Heart,
      title: t('values.userFocused.title'),
      description: t('values.userFocused.description')
    },
    {
      icon: Zap,
      title: t('values.innovation.title'),
      description: t('values.innovation.description')
    },
    {
      icon: Shield,
      title: t('values.transparency.title'),
      description: t('values.transparency.description')
    }
  ];

  const team = [
    {
      name: t('team.sarah.name'),
      role: t('team.sarah.role'),
      bio: t('team.sarah.bio'),
      avatar: '👩‍💼'
    },
    {
      name: t('team.tom.name'),
      role: t('team.tom.role'),
      bio: t('team.tom.bio'),
      avatar: '👨‍💻'
    },
    {
      name: t('team.lisa.name'),
      role: t('team.lisa.role'),
      bio: t('team.lisa.bio'),
      avatar: '👩‍🔬'
    },
    {
      name: t('team.marc.name'),
      role: t('team.marc.role'),
      bio: t('team.marc.bio'),
      avatar: '👨‍💼'
    }
  ];

  const stats = [
    { number: '50.000+', label: t('stats.users') },
    { number: '11.000+', label: t('stats.neighborhoods') },
    { number: '1M+', label: t('stats.searches') },
    { number: '99.9%', label: t('stats.availability') }
  ];

  return (
    <div className="min-h-screen bg-neutral-50">
      {/* Hero Section */}
      <div className="bg-white border-b border-neutral-200">
        <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8 py-16">
          <div className="text-center">
            <h1 className="text-4xl sm:text-5xl font-bold text-neutral-900 mb-6">
              {t('title')}
            </h1>
            <p className="text-xl text-neutral-600 max-w-3xl mx-auto mb-8">
              {t('subtitle')}
            </p>
            <div className="flex items-center justify-center space-x-8">
              <div className="flex items-center">
                <MapPin className="h-8 w-8 text-primary-600 mr-2" />
                <span className="text-lg font-medium text-neutral-900">{t('founded')}</span>
              </div>
              <div className="flex items-center">
                <Globe className="h-8 w-8 text-primary-600 mr-2" />
                <span className="text-lg font-medium text-neutral-900">{t('location')}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Mission Section */}
      <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8 py-16">
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-16 items-center">
          <div>
            <h2 className="text-3xl font-bold text-neutral-900 mb-6">{t('mission.title')}</h2>
            <p className="text-lg text-neutral-600 mb-6">
              {t('mission.paragraph1')}
            </p>
            <p className="text-lg text-neutral-600 mb-6">
              {t('mission.paragraph2')}
            </p>
            <Link href="/search">
              <Button variant="primary" size="lg">
                {t('mission.cta')}
              </Button>
            </Link>
          </div>
          <div className="bg-gradient-to-r from-primary-50 to-blue-50 rounded-2xl p-8">
            <h3 className="text-xl font-semibold text-neutral-900 mb-4">{t('unique.title')}</h3>
            <ul className="space-y-3">
              <li className="flex items-start">
                <Award className="h-5 w-5 text-primary-600 mr-3 mt-0.5 flex-shrink-0" />
                <span className="text-neutral-700">{t('unique.item1')}</span>
              </li>
              <li className="flex items-start">
                <Users className="h-5 w-5 text-primary-600 mr-3 mt-0.5 flex-shrink-0" />
                <span className="text-neutral-700">{t('unique.item2')}</span>
              </li>
              <li className="flex items-start">
                <MapPin className="h-5 w-5 text-primary-600 mr-3 mt-0.5 flex-shrink-0" />
                <span className="text-neutral-700">{t('unique.item3')}</span>
              </li>
              <li className="flex items-start">
                <Zap className="h-5 w-5 text-primary-600 mr-3 mt-0.5 flex-shrink-0" />
                <span className="text-neutral-700">{t('unique.item4')}</span>
              </li>
            </ul>
          </div>
        </div>
      </div>

      {/* Values Section */}
      <div className="bg-white">
        <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8 py-16">
          <div className="text-center mb-12">
            <h2 className="text-3xl font-bold text-neutral-900 mb-4">{t('values.title')}</h2>
            <p className="text-lg text-neutral-600 max-w-2xl mx-auto">
              {t('values.subtitle')}
            </p>
          </div>
          
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8">
            {values.map((value, index) => (
              <div key={index} className="text-center">
                <div className="bg-primary-50 rounded-full w-16 h-16 flex items-center justify-center mx-auto mb-4">
                  <value.icon className="h-8 w-8 text-primary-600" />
                </div>
                <h3 className="text-lg font-semibold text-neutral-900 mb-2">{value.title}</h3>
                <p className="text-neutral-600">{value.description}</p>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Stats Section */}
      <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8 py-16">
        <div className="text-center mb-12">
          <h2 className="text-3xl font-bold text-neutral-900 mb-4">{t('stats.title')}</h2>
          <p className="text-lg text-neutral-600">
            {t('stats.subtitle')}
          </p>
        </div>
        
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-8">
          {stats.map((stat, index) => (
            <div key={index} className="text-center bg-white rounded-lg p-6 shadow-sm border border-neutral-200">
              <div className="text-3xl font-bold text-primary-600 mb-2">{stat.number}</div>
              <div className="text-neutral-600">{stat.label}</div>
            </div>
          ))}
        </div>
      </div>

      {/* Team Section */}
      <div className="bg-white">
        <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8 py-16">
          <div className="text-center mb-12">
            <h2 className="text-3xl font-bold text-neutral-900 mb-4">{t('team.title')}</h2>
            <p className="text-lg text-neutral-600 max-w-2xl mx-auto">
              {t('team.subtitle')}
            </p>
          </div>
          
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8">
            {team.map((member, index) => (
              <div key={index} className="text-center">
                <div className="text-6xl mb-4">{member.avatar}</div>
                <h3 className="text-lg font-semibold text-neutral-900 mb-1">{member.name}</h3>
                <p className="text-primary-600 font-medium mb-2">{member.role}</p>
                <p className="text-sm text-neutral-600">{member.bio}</p>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* CTA Section */}
      <div className="bg-primary-600">
        <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8 py-16">
          <div className="text-center text-white">
            <h2 className="text-3xl font-bold mb-4">{t('cta.title')}</h2>
            <p className="text-xl text-primary-100 mb-8 max-w-2xl mx-auto">
              {t('cta.subtitle')}
            </p>
            <div className="flex flex-col sm:flex-row gap-4 justify-center">
              <Link href="/search">
                <Button variant="secondary" size="lg">
                  {t('cta.start')}
                </Button>
              </Link>
              <Link href="/contact">
                <Button variant="outline" size="lg" className="border-white text-white hover:bg-white hover:text-primary-600">
                  {t('cta.contact')}
                </Button>
              </Link>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}