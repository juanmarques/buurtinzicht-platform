'use client';

import React, { useState } from 'react';
import Link from 'next/link';
import { ChevronDown, ChevronUp, HelpCircle, Search, MessageSquare } from 'lucide-react';
import { Button } from '../../components/ui/Button';
import { Input } from '../../components/ui/Input';
import { useTranslations } from 'next-intl';

interface FAQ {
  id: string;
  question: string;
  answer: string;
  category: string;
}

export default function FAQPage() {
  const t = useTranslations('faq');
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('all');
  const [openItems, setOpenItems] = useState<Set<string>>(new Set());

  const faqs: FAQ[] = [
    // General
    {
      id: '1',
      category: 'general',
      question: t('items.whatIs.question'),
      answer: t('items.whatIs.answer')
    },
    {
      id: '2',
      category: 'general',
      question: t('items.howItWorks.question'),
      answer: t('items.howItWorks.answer')
    },
    {
      id: '3',
      category: 'general',
      question: t('items.coverage.question'),
      answer: t('items.coverage.answer')
    },
    
    // Data
    {
      id: '4',
      category: 'data',
      question: t('items.accuracy.question'),
      answer: t('items.accuracy.answer')
    },
    {
      id: '5',
      category: 'data',
      question: t('items.updates.question'),
      answer: t('items.updates.answer')
    },
    {
      id: '6',
      category: 'data',
      question: t('items.contribute.question'),
      answer: t('items.contribute.answer')
    },
    
    // Features
    {
      id: '7',
      category: 'features',
      question: t('items.preferences.question'),
      answer: t('items.preferences.answer')
    },
    {
      id: '8',
      category: 'features',
      question: t('items.scorecard.question'),
      answer: t('items.scorecard.answer')
    },
    {
      id: '9',
      category: 'features',
      question: t('items.compare.question'),
      answer: t('items.compare.answer')
    },
    {
      id: '10',
      category: 'features',
      question: t('items.share.question'),
      answer: t('items.share.answer')
    },
    
    // Pricing
    {
      id: '11',
      category: 'pricing',
      question: t('items.cost.question'),
      answer: t('items.cost.answer')
    },
    {
      id: '12',
      category: 'pricing',
      question: t('items.trial.question'),
      answer: t('items.trial.answer')
    },
    {
      id: '13',
      category: 'pricing',
      question: t('items.cancel.question'),
      answer: t('items.cancel.answer')
    },
    {
      id: '14',
      category: 'pricing',
      question: t('items.payment.question'),
      answer: t('items.payment.answer')
    },
    
    // Technical
    {
      id: '15',
      category: 'technical',
      question: t('items.mobile.question'),
      answer: t('items.mobile.answer')
    },
    {
      id: '16',
      category: 'technical',
      question: t('items.browsers.question'),
      answer: t('items.browsers.answer')
    },
    {
      id: '17',
      category: 'technical',
      question: t('items.api.question'),
      answer: t('items.api.answer')
    }
  ];

  const categories = [
    { value: 'all', label: t('categories.all') },
    { value: 'general', label: t('categories.general') },
    { value: 'data', label: t('categories.data') },
    { value: 'features', label: t('categories.features') },
    { value: 'pricing', label: t('categories.pricing') },
    { value: 'technical', label: t('categories.technical') }
  ];

  const filteredFAQs = faqs.filter(faq => {
    const matchesSearch = searchTerm === '' || 
      faq.question.toLowerCase().includes(searchTerm.toLowerCase()) ||
      faq.answer.toLowerCase().includes(searchTerm.toLowerCase());
    
    const matchesCategory = selectedCategory === 'all' || faq.category === selectedCategory;
    
    return matchesSearch && matchesCategory;
  });

  const toggleFAQ = (id: string) => {
    const newOpenItems = new Set(openItems);
    if (newOpenItems.has(id)) {
      newOpenItems.delete(id);
    } else {
      newOpenItems.add(id);
    }
    setOpenItems(newOpenItems);
  };

  const getCategoryLabel = (category: string) => {
    return categories.find(cat => cat.value === category)?.label || category;
  };

  return (
    <div className="min-h-screen bg-neutral-50">
      {/* Header */}
      <div className="bg-white border-b border-neutral-200">
        <div className="mx-auto max-w-4xl px-4 sm:px-6 lg:px-8 py-16">
          <div className="text-center">
            <div className="flex items-center justify-center mb-4">
              <HelpCircle className="h-10 w-10 text-primary-600 mr-3" />
              <h1 className="text-4xl font-bold text-neutral-900">{t('title')}</h1>
            </div>
            <p className="text-lg text-neutral-600 max-w-3xl mx-auto">
              {t('subtitle')}
            </p>
          </div>
        </div>
      </div>

      <div className="mx-auto max-w-4xl px-4 sm:px-6 lg:px-8 py-16">
        {/* Search and Filter */}
        <div className="bg-white rounded-lg shadow-sm border border-neutral-200 p-6 mb-8">
          <div className="flex flex-col md:flex-row gap-4">
            <div className="flex-1">
              <div className="relative">
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-neutral-400" />
                <input
                  type="text"
                  placeholder={t('searchPlaceholder')}
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  className="w-full pl-10 pr-3 py-2 border border-neutral-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
                />
              </div>
            </div>
            <div className="md:w-64">
              <select
                value={selectedCategory}
                onChange={(e) => setSelectedCategory(e.target.value)}
                className="w-full px-3 py-2 border border-neutral-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
              >
                {categories.map(category => (
                  <option key={category.value} value={category.value}>
                    {category.label}
                  </option>
                ))}
              </select>
            </div>
          </div>
        </div>

        {/* FAQ Results */}
        {filteredFAQs.length === 0 ? (
          <div className="text-center py-16">
            <Search className="h-16 w-16 text-neutral-400 mx-auto mb-4" />
            <h2 className="text-2xl font-bold text-neutral-900 mb-2">
              {t('noResults.title')}
            </h2>
            <p className="text-neutral-600 mb-6">
              {t('noResults.subtitle')}
            </p>
            <div className="flex justify-center gap-4">
              <Button variant="outline" onClick={() => setSearchTerm('')}>
                {t('noResults.clearSearch')}
              </Button>
              <Button variant="outline" onClick={() => setSelectedCategory('all')}>
                {t('noResults.allCategories')}
              </Button>
            </div>
          </div>
        ) : (
          <div className="space-y-4">
            {filteredFAQs.map((faq) => (
              <div key={faq.id} className="bg-white rounded-lg shadow-sm border border-neutral-200">
                <button
                  onClick={() => toggleFAQ(faq.id)}
                  className="w-full px-6 py-4 text-left flex items-center justify-between hover:bg-neutral-50 transition-colors"
                >
                  <div className="flex-1">
                    <div className="flex items-center mb-1">
                      <span className="text-xs px-2 py-1 bg-primary-100 text-primary-700 rounded-full mr-2">
                        {getCategoryLabel(faq.category)}
                      </span>
                    </div>
                    <h3 className="text-lg font-semibold text-neutral-900">{faq.question}</h3>
                  </div>
                  {openItems.has(faq.id) ? (
                    <ChevronUp className="h-5 w-5 text-neutral-500 ml-4 flex-shrink-0" />
                  ) : (
                    <ChevronDown className="h-5 w-5 text-neutral-500 ml-4 flex-shrink-0" />
                  )}
                </button>
                
                {openItems.has(faq.id) && (
                  <div className="px-6 pb-4">
                    <div className="border-t border-neutral-200 pt-4">
                      <p className="text-neutral-700 leading-relaxed">{faq.answer}</p>
                    </div>
                  </div>
                )}
              </div>
            ))}
          </div>
        )}

        {/* Quick Stats */}
        <div className="mt-12 grid grid-cols-1 md:grid-cols-3 gap-6">
          <div className="text-center p-6 bg-white rounded-lg shadow-sm border border-neutral-200">
            <HelpCircle className="h-8 w-8 text-primary-600 mx-auto mb-2" />
            <div className="text-2xl font-bold text-neutral-900">{faqs.length}</div>
            <div className="text-sm text-neutral-600">{t('stats.articles')}</div>
          </div>
          <div className="text-center p-6 bg-white rounded-lg shadow-sm border border-neutral-200">
            <Search className="h-8 w-8 text-primary-600 mx-auto mb-2" />
            <div className="text-2xl font-bold text-neutral-900">{categories.length - 1}</div>
            <div className="text-sm text-neutral-600">{t('stats.categories')}</div>
          </div>
          <div className="text-center p-6 bg-white rounded-lg shadow-sm border border-neutral-200">
            <MessageSquare className="h-8 w-8 text-primary-600 mx-auto mb-2" />
            <div className="text-2xl font-bold text-neutral-900">24h</div>
            <div className="text-sm text-neutral-600">{t('stats.responseTime')}</div>
          </div>
        </div>

        {/* Contact CTA */}
        <div className="bg-primary-600 rounded-lg text-white p-8 text-center mt-12">
          <h2 className="text-2xl font-bold mb-4">{t('cta.title')}</h2>
          <p className="text-primary-100 mb-6">
            {t('cta.subtitle')}
          </p>
          <div className="flex flex-col sm:flex-row gap-4 justify-center">
            <Link href="/contact">
              <Button variant="secondary" size="lg">
                <MessageSquare className="h-4 w-4 mr-2" />
                {t('cta.contact')}
              </Button>
            </Link>
            <Button variant="outline" size="lg" className="border-white text-white hover:bg-white hover:text-primary-600">
              <HelpCircle className="h-4 w-4 mr-2" />
              {t('cta.chat')}
            </Button>
          </div>
        </div>
      </div>
    </div>
  );
}