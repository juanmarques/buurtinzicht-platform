'use client';

import React from 'react';
import { Shield, Eye, Lock, Users, FileText, Mail, Calendar } from 'lucide-react';
import { useTranslations } from 'next-intl';

export default function PrivacyPage() {
  const t = useTranslations('privacy');
  const lastUpdated = '20 januari 2024';

  const sections = [
    {
      title: t('sections.what.title'),
      icon: FileText,
      content: [
        t('sections.what.content.account'),
        t('sections.what.content.history'),
        t('sections.what.content.technical'),
        t('sections.what.content.usage'),
        t('sections.what.content.cookies')
      ]
    },
    {
      title: t('sections.how.title'),
      icon: Eye,
      content: [
        t('sections.how.content.recommendations'),
        t('sections.how.content.history'),
        t('sections.how.content.improve'),
        t('sections.how.content.communication'),
        t('sections.how.content.support'),
        t('sections.how.content.legal')
      ]
    },
    {
      title: t('sections.share.title'),
      icon: Users,
      content: [
        t('sections.share.content.noSell'),
        t('sections.share.content.anonymized'),
        t('sections.share.content.serviceProviders'),
        t('sections.share.content.legal'),
        t('sections.share.content.takeover')
      ]
    },
    {
      title: t('sections.security.title'),
      icon: Lock,
      content: [
        t('sections.security.content.ssl'),
        t('sections.security.content.hashing'),
        t('sections.security.content.audits'),
        t('sections.security.content.access'),
        t('sections.security.content.backups'),
        t('sections.security.content.gdpr')
      ]
    }
  ];

  return (
    <div className="min-h-screen bg-neutral-50">
      {/* Header */}
      <div className="bg-white border-b border-neutral-200">
        <div className="mx-auto max-w-4xl px-4 sm:px-6 lg:px-8 py-16">
          <div className="text-center">
            <div className="flex items-center justify-center mb-4">
              <Shield className="h-10 w-10 text-primary-600 mr-3" />
              <h1 className="text-4xl font-bold text-neutral-900">{t('title')}</h1>
            </div>
            <p className="text-lg text-neutral-600 max-w-3xl mx-auto">
              {t('subtitle')}
            </p>
            <div className="flex items-center justify-center mt-6 text-sm text-neutral-500">
              <Calendar className="h-4 w-4 mr-2" />
              {t('lastUpdated', { date: lastUpdated })}
            </div>
          </div>
        </div>
      </div>

      <div className="mx-auto max-w-4xl px-4 sm:px-6 lg:px-8 py-16">
        {/* Introduction */}
        <div className="bg-primary-50 border border-primary-200 rounded-lg p-6 mb-12">
          <h2 className="text-xl font-semibold text-primary-900 mb-3">{t('intro.title')}</h2>
          <p className="text-primary-800 mb-4">
            {t('intro.paragraph')}
          </p>
          <ul className="text-primary-800 space-y-1 text-sm">
            <li>• {t('intro.bullet1')}</li>
            <li>• {t('intro.bullet2')}</li>
            <li>• {t('intro.bullet3')}</li>
            <li>• {t('intro.bullet4')}</li>
          </ul>
        </div>

        {/* Main Sections */}
        <div className="space-y-12">
          {sections.map((section, index) => (
            <div key={index} className="bg-white rounded-lg shadow-sm border border-neutral-200 p-8">
              <div className="flex items-center mb-6">
                <div className="bg-primary-50 rounded-lg p-3 mr-4">
                  <section.icon className="h-6 w-6 text-primary-600" />
                </div>
                <h2 className="text-2xl font-semibold text-neutral-900">{section.title}</h2>
              </div>
              <ul className="space-y-3">
                {section.content.map((item, itemIndex) => (
                  <li key={itemIndex} className="flex items-start">
                    <div className="w-2 h-2 bg-primary-600 rounded-full mr-3 mt-2 flex-shrink-0"></div>
                    <span className="text-neutral-700">{item}</span>
                  </li>
                ))}
              </ul>
            </div>
          ))}
        </div>

        {/* Your Rights */}
        <div className="bg-white rounded-lg shadow-sm border border-neutral-200 p-8 mt-12">
          <div className="flex items-center mb-6">
            <div className="bg-green-50 rounded-lg p-3 mr-4">
              <Shield className="h-6 w-6 text-green-600" />
            </div>
            <h2 className="text-2xl font-semibold text-neutral-900">{t('rights.title')}</h2>
          </div>
          
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <h3 className="font-semibold text-neutral-900 mb-3">{t('rights.access.title')}</h3>
              <ul className="space-y-2 text-neutral-700">
                <li className="flex items-start">
                  <Eye className="h-4 w-4 text-green-600 mr-2 mt-0.5 flex-shrink-0" />
                  <span>{t('rights.access.item1')}</span>
                </li>
                <li className="flex items-start">
                  <FileText className="h-4 w-4 text-green-600 mr-2 mt-0.5 flex-shrink-0" />
                  <span>{t('rights.access.item2')}</span>
                </li>
                <li className="flex items-start">
                  <Lock className="h-4 w-4 text-green-600 mr-2 mt-0.5 flex-shrink-0" />
                  <span>{t('rights.access.item3')}</span>
                </li>
              </ul>
            </div>
            <div>
              <h3 className="font-semibold text-neutral-900 mb-3">{t('rights.erasure.title')}</h3>
              <ul className="space-y-2 text-neutral-700">
                <li className="flex items-start">
                  <Users className="h-4 w-4 text-green-600 mr-2 mt-0.5 flex-shrink-0" />
                  <span>{t('rights.erasure.item1')}</span>
                </li>
                <li className="flex items-start">
                  <Shield className="h-4 w-4 text-green-600 mr-2 mt-0.5 flex-shrink-0" />
                  <span>{t('rights.erasure.item2')}</span>
                </li>
                <li className="flex items-start">
                  <Mail className="h-4 w-4 text-green-600 mr-2 mt-0.5 flex-shrink-0" />
                  <span>{t('rights.erasure.item3')}</span>
                </li>
              </ul>
            </div>
          </div>
          
          <div className="bg-neutral-50 rounded-lg p-4 mt-6">
            <p className="text-sm text-neutral-600">
              <strong>{t('rights.exercise')}:</strong> {t.rich('rights.email', { a: (chunks) => <a href="mailto:privacy@buurtinzicht.be" className="text-primary-600 hover:text-primary-700">{chunks}</a> })}
            </p>
          </div>
        </div>

        {/* Cookies */}
        <div className="bg-white rounded-lg shadow-sm border border-neutral-200 p-8 mt-12">
          <h2 className="text-2xl font-semibold text-neutral-900 mb-6">{t('cookies.title')}</h2>
          
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-6">
            <div className="bg-green-50 border border-green-200 rounded-lg p-4">
              <h3 className="font-semibold text-green-900 mb-2">{t('cookies.essential.title')}</h3>
              <p className="text-sm text-green-700">
                {t('cookies.essential.description')}
              </p>
            </div>
            <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
              <h3 className="font-semibold text-blue-900 mb-2">{t('cookies.analytical.title')}</h3>
              <p className="text-sm text-blue-700">
                {t('cookies.analytical.description')}
              </p>
            </div>
            <div className="bg-purple-50 border border-purple-200 rounded-lg p-4">
              <h3 className="font-semibold text-purple-900 mb-2">{t('cookies.marketing.title')}</h3>
              <p className="text-sm text-purple-700">
                {t('cookies.marketing.description')}
              </p>
            </div>
          </div>
          
          <p className="text-neutral-600 text-sm">
            {t.rich('cookies.manage', { a: (chunks) => <a href="/dashboard/settings" className="text-primary-600 hover:text-primary-700">{chunks}</a> })}
          </p>
        </div>

        {/* Data Retention */}
        <div className="bg-white rounded-lg shadow-sm border border-neutral-200 p-8 mt-12">
          <h2 className="text-2xl font-semibold text-neutral-900 mb-6">{t('retention.title')}</h2>
          
          <div className="space-y-4">
            <div className="flex justify-between items-center py-3 border-b border-neutral-200">
              <span className="font-medium text-neutral-900">{t('retention.account.label')}</span>
              <span className="text-neutral-600">{t('retention.account.value')}</span>
            </div>
            <div className="flex justify-between items-center py-3 border-b border-neutral-200">
              <span className="font-medium text-neutral-900">{t('retention.history.label')}</span>
              <span className="text-neutral-600">{t('retention.history.value')}</span>
            </div>
            <div className="flex justify-between items-center py-3 border-b border-neutral-200">
              <span className="font-medium text-neutral-900">{t('retention.logs.label')}</span>
              <span className="text-neutral-600">{t('retention.logs.value')}</span>
            </div>
            <div className="flex justify-between items-center py-3 border-b border-neutral-200">
              <span className="font-medium text-neutral-900">{t('retention.marketing.label')}</span>
              <span className="text-neutral-600">{t('retention.marketing.value')}</span>
            </div>
            <div className="flex justify-between items-center py-3">
              <span className="font-medium text-neutral-900">{t('retention.anonymized.label')}</span>
              <span className="text-neutral-600">{t('retention.anonymized.value')}</span>
            </div>
          </div>
        </div>

        {/* Contact Information */}
        <div className="bg-primary-600 rounded-lg text-white p-8 mt-12">
          <h2 className="text-2xl font-semibold mb-4">{t('questions.title')}</h2>
          <p className="text-primary-100 mb-6">
            {t('questions.subtitle')}
          </p>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <h3 className="font-semibold mb-2">{t('questions.dpo.title')}</h3>
              <p className="text-primary-100">
                <Mail className="inline h-4 w-4 mr-1" />
                privacy@buurtinzicht.be
              </p>
            </div>
            <div>
              <h3 className="font-semibold mb-2">{t('questions.general.title')}</h3>
              <p className="text-primary-100">
                <Mail className="inline h-4 w-4 mr-1" />
                info@buurtinzicht.be
              </p>
            </div>
          </div>
          <div className="bg-primary-700 rounded-lg p-4 mt-6">
            <p className="text-sm text-primary-100">
              <strong>{t('questions.complaint.title')}:</strong> {t.rich('questions.complaint.content', { a: (chunks) => <a href="https://www.gegevensbeschermingsautoriteit.be" className="text-white hover:underline">{chunks}</a> })}
            </p>
          </div>
        </div>

        {/* Updates */}
        <div className="bg-neutral-100 rounded-lg p-6 mt-12">
          <h2 className="text-lg font-semibold text-neutral-900 mb-3">{t('updates.title')}</h2>
          <p className="text-neutral-700 text-sm">
            {t('updates.content')}
          </p>
        </div>
      </div>
    </div>
  );
}