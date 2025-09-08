'use client';

import React, { useState } from 'react';
import Link from 'next/link';
import { 
  Check, 
  X, 
  Star, 
  Zap,
  Users,
  Shield,
  BarChart3,
  MapPin,
  Heart,
  Crown,
  Sparkles
} from 'lucide-react';
import { Button } from '../../components/ui/Button';
import { useTranslations } from 'next-intl';
import { useFormatters } from '../../lib/formatters';

interface PricingPlan {
  id: string;
  name: string;
  description: string;
  price: {
    monthly: number;
    yearly: number;
  };
  features: {
    name: string;
    included: boolean;
    description?: string;
  }[];
  highlighted: boolean;
  icon: React.ElementType;
  color: string;
}

export default function PricingPage() {
  const t = useTranslations('pricing');
  const { formatCurrency, formatPercent } = useFormatters();
  const [billingCycle, setBillingCycle] = useState<'monthly' | 'yearly'>('monthly');

  const plans: PricingPlan[] = [
    {
      id: 'free',
      name: t('plans.free.name'),
      description: t('plans.free.description'),
      price: {
        monthly: 0,
        yearly: 0
      },
      features: [
        { name: t('plans.free.features.searches'), included: true },
        { name: t('plans.free.features.comparisons'), included: true },
        { name: t('plans.free.features.basicInfo'), included: true },
        { name: t('plans.free.features.publicMaps'), included: true },
        { name: t('plans.free.features.favorites'), included: true },
        { name: t('plans.free.features.scorecards'), included: false },
        { name: t('plans.free.features.unlimitedSearches'), included: false },
        { name: t('plans.free.features.premiumData'), included: false },
        { name: t('plans.free.features.apiAccess'), included: false },
        { name: t('plans.free.features.prioritySupport'), included: false }
      ],
      highlighted: false,
      icon: Heart,
      color: 'text-neutral-600'
    },
    {
      id: 'pro',
      name: t('plans.pro.name'),
      description: t('plans.pro.description'),
      price: {
        monthly: 19,
        yearly: 190
      },
      features: [
        { name: t('plans.pro.features.unlimitedSearches'), included: true },
        { name: t('plans.pro.features.unlimitedComparisons'), included: true },
        { name: t('plans.pro.features.detailedScorecards'), included: true },
        { name: t('plans.pro.features.premiumMaps'), included: true },
        { name: t('plans.pro.features.favorites'), included: true },
        { name: t('plans.pro.features.historicalData'), included: true },
        { name: t('plans.pro.features.personalizedRecommendations'), included: true },
        { name: t('plans.pro.features.export'), included: true },
        { name: t('plans.pro.features.apiAccess'), included: false },
        { name: t('plans.pro.features.prioritySupport'), included: false }
      ],
      highlighted: true,
      icon: Star,
      color: 'text-primary-600'
    },
    {
      id: 'business',
      name: t('plans.business.name'),
      description: t('plans.business.description'),
      price: {
        monthly: 49,
        yearly: 490
      },
      features: [
        { name: t('plans.business.features.allPro'), included: true },
        { name: t('plans.business.features.fullApi'), included: true },
        { name: t('plans.business.features.bulkExport'), included: true },
        { name: t('plans.business.features.customReports'), included: true },
        { name: t('plans.business.features.teamCollaboration'), included: true },
        { name: t('plans.business.features.whiteLabel'), included: true },
        { name: t('plans.business.features.prioritySupport'), included: true },
        { name: t('plans.business.features.training'), included: true },
        { name: t('plans.business.features.customIntegrations'), included: true },
        { name: t('plans.business.features.sla'), included: true }
      ],
      highlighted: false,
      icon: Crown,
      color: 'text-yellow-600'
    }
  ];

  const featuresComparison = [
    {
      category: t('comparison.search.category'),
      features: [
        { name: t('comparison.search.features.monthlySearches'), free: '5', pro: t('comparison.unlimited'), business: t('comparison.unlimited') },
        { name: t('comparison.search.features.advancedFilters'), free: t('comparison.basic'), pro: t('comparison.full'), business: t('comparison.fullCustom') },
        { name: t('comparison.search.features.radiusSearch'), free: '✓', pro: '✓', business: '✓' },
        { name: t('comparison.search.features.locationSearch'), free: '✓', pro: '✓', business: '✓' }
      ]
    },
    {
      category: t('comparison.data.category'),
      features: [
        { name: t('comparison.data.features.basicInfo'), free: '✓', pro: '✓', business: '✓' },
        { name: t('comparison.data.features.detailedScorecards'), free: '✗', pro: '✓', business: '✓' },
        { name: t('comparison.data.features.historicalTrends'), free: '✗', pro: '✓', business: '✓' },
        { name: t('comparison.data.features.predictiveAnalytics'), free: '✗', pro: '✗', business: '✓' }
      ]
    },
    {
      category: t('comparison.tools.category'),
      features: [
        { name: t('comparison.tools.features.comparisons'), free: t('comparison.perMonth', { count: 2 }), pro: t('comparison.unlimited'), business: t('comparison.unlimited') },
        { name: t('comparison.tools.features.favorites'), free: '3', pro: '50', business: t('comparison.unlimited') },
        { name: t('comparison.tools.features.export'), free: '✗', pro: 'PDF/CSV', business: t('comparison.allFormats') },
        { name: t('comparison.tools.features.teamCollaboration'), free: '✗', pro: '✗', business: '✓' }
      ]
    }
  ];

  const getPrice = (plan: PricingPlan) => {
    const price = billingCycle === 'yearly' ? plan.price.yearly : plan.price.monthly;
    return price;
  };

  const getMonthlyPrice = (plan: PricingPlan) => {
    const price = billingCycle === 'yearly' ? plan.price.yearly / 12 : plan.price.monthly;
    return price;
  };

  const getSavings = (plan: PricingPlan) => {
    if (plan.price.monthly === 0) return 0;
    const yearlyMonthly = plan.price.yearly / 12;
    const savings = ((plan.price.monthly - yearlyMonthly) / plan.price.monthly) * 100;
    return Math.round(savings);
  };

  return (
    <div className="min-h-screen bg-neutral-50">
      {/* Header */}
      <div className="bg-white border-b border-neutral-200">
        <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8 py-16">
          <div className="text-center">
            <h1 className="text-4xl sm:text-5xl font-bold text-neutral-900 mb-4">
              {t('title')}
            </h1>
            <p className="text-xl text-neutral-600 max-w-3xl mx-auto mb-8">
              {t('subtitle')}
            </p>
            
            {/* Billing Toggle */}
            <div className="flex items-center justify-center mb-8">
              <span className={`mr-3 ${billingCycle === 'monthly' ? 'text-neutral-900 font-medium' : 'text-neutral-600'}`}>
                {t('billing.monthly')}
              </span>
              <button
                onClick={() => setBillingCycle(billingCycle === 'monthly' ? 'yearly' : 'monthly')}
                className={`relative inline-flex h-6 w-11 items-center rounded-full transition-colors ${
                  billingCycle === 'yearly' ? 'bg-primary-600' : 'bg-neutral-300'
                }`}
              >
                <span
                  className={`inline-block h-4 w-4 transform rounded-full bg-white transition-transform ${
                    billingCycle === 'yearly' ? 'translate-x-6' : 'translate-x-1'
                  }`}
                />
              </button>
              <span className={`ml-3 ${billingCycle === 'yearly' ? 'text-neutral-900 font-medium' : 'text-neutral-600'}`}>
                {t('billing.yearly')}
              </span>
              {billingCycle === 'yearly' && (
                <span className="ml-2 bg-green-100 text-green-800 text-sm px-2 py-1 rounded-full">
                  {t('billing.save', { percentage: 17 })}
                </span>
              )}
            </div>
          </div>
        </div>
      </div>

      <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8 py-16">
        {/* Pricing Cards */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8 mb-16">
          {plans.map((plan) => {
            const Icon = plan.icon;
            return (
              <div
                key={plan.id}
                className={`bg-white rounded-2xl shadow-sm border-2 transition-all hover:shadow-lg ${
                  plan.highlighted
                    ? 'border-primary-200 ring-4 ring-primary-50 relative'
                    : 'border-neutral-200'
                }`}
              >
                {plan.highlighted && (
                  <div className="absolute -top-4 left-1/2 transform -translate-x-1/2">
                    <div className="bg-primary-600 text-white px-4 py-1 rounded-full text-sm font-medium flex items-center">
                      <Sparkles className="h-4 w-4 mr-1" />
                      {t('mostPopular')}
                    </div>
                  </div>
                )}
                
                <div className="p-8">
                  {/* Header */}
                  <div className="text-center mb-8">
                    <Icon className={`h-12 w-12 mx-auto mb-4 ${plan.color}`} />
                    <h3 className="text-2xl font-bold text-neutral-900 mb-2">{plan.name}</h3>
                    <p className="text-neutral-600 mb-6">{plan.description}</p>
                    
                    <div className="mb-4">
                      <div className="text-4xl font-bold text-neutral-900">
                        {plan.price.monthly === 0 
                          ? t('plans.free.name')
                          : formatCurrency(getMonthlyPrice(plan))
                        }
                        {plan.price.monthly > 0 && <span className="text-lg text-neutral-600">/{t('perMonth')}</span>}
                      </div>
                      {billingCycle === 'yearly' && plan.price.monthly > 0 && (
                        <div className="text-sm text-neutral-600">
                          {formatCurrency(getPrice(plan))} {t('perYear')} • {t('save')} {formatPercent(getSavings(plan))}
                        </div>
                      )}
                    </div>
                    
                    <Button
                      variant={plan.highlighted ? 'primary' : 'outline'}
                      className="w-full"
                      size="lg"
                    >
                      {plan.id === 'free' ? t('cta.startFree') : t('cta.startToday')}
                    </Button>
                  </div>
                  
                  {/* Features */}
                  <div className="space-y-3">
                    {plan.features.map((feature, idx) => (
                      <div key={idx} className="flex items-start">
                        {feature.included ? (
                          <Check className="h-5 w-5 text-green-500 mr-3 mt-0.5 flex-shrink-0" />
                        ) : (
                          <X className="h-5 w-5 text-neutral-400 mr-3 mt-0.5 flex-shrink-0" />
                        )}
                        <span className={feature.included ? 'text-neutral-900' : 'text-neutral-500'}>
                          {feature.name}
                        </span>
                      </div>
                    ))}
                  </div>
                </div>
              </div>
            );
          })}
        </div>

        {/* Feature Comparison Table */}
        <div className="bg-white rounded-2xl shadow-sm border border-neutral-200 overflow-hidden mb-16">
          <div className="p-8 border-b border-neutral-200">
            <h2 className="text-2xl font-bold text-neutral-900 mb-2">{t('comparison.title')}</h2>
            <p className="text-neutral-600">{t('comparison.subtitle')}</p>
          </div>
          
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead className="bg-neutral-50">
                <tr>
                  <th className="px-8 py-4 text-left text-sm font-medium text-neutral-900">{t('comparison.features')}</th>
                  <th className="px-8 py-4 text-center text-sm font-medium text-neutral-900">{t('plans.free.name')}</th>
                  <th className="px-8 py-4 text-center text-sm font-medium text-neutral-900">{t('plans.pro.name')}</th>
                  <th className="px-8 py-4 text-center text-sm font-medium text-neutral-900">{t('plans.business.name')}</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-neutral-200">
                {featuresComparison.map((category, categoryIdx) => (
                  <React.Fragment key={categoryIdx}>
                    <tr className="bg-neutral-25">
                      <td colSpan={4} className="px-8 py-3">
                        <h4 className="font-semibold text-neutral-900">{category.category}</h4>
                      </td>
                    </tr>
                    {category.features.map((feature, featureIdx) => (
                      <tr key={featureIdx}>
                        <td className="px-8 py-4 text-sm text-neutral-900">{feature.name}</td>
                        <td className="px-8 py-4 text-sm text-center text-neutral-600">{feature.free}</td>
                        <td className="px-8 py-4 text-sm text-center text-neutral-600">{feature.pro}</td>
                        <td className="px-8 py-4 text-sm text-center text-neutral-600">{feature.business}</td>
                      </tr>
                    ))}
                  </React.Fragment>
                ))}
              </tbody>
            </table>
          </div>
        </div>

        {/* FAQ */}
        <div className="max-w-4xl mx-auto">
          <h2 className="text-2xl font-bold text-neutral-900 text-center mb-8">{t('faq.title')}</h2>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
            <div>
              <h3 className="font-semibold text-neutral-900 mb-2">{t('faq.q1.question')}</h3>
              <p className="text-neutral-600 text-sm">
                {t('faq.q1.answer')}
              </p>
            </div>
            <div>
              <h3 className="font-semibold text-neutral-900 mb-2">{t('faq.q2.question')}</h3>
              <p className="text-neutral-600 text-sm">
                {t('faq.q2.answer')}
              </p>
            </div>
            <div>
              <h3 className="font-semibold text-neutral-900 mb-2">{t('faq.q3.question')}</h3>
              <p className="text-neutral-600 text-sm">
                {t('faq.q3.answer')}
              </p>
            </div>
            <div>
              <h3 className="font-semibold text-neutral-900 mb-2">{t('faq.q4.question')}</h3>
              <p className="text-neutral-600 text-sm">
                {t('faq.q4.answer')}
              </p>
            </div>
          </div>
        </div>

        {/* CTA */}
        <div className="bg-primary-600 rounded-2xl p-8 text-center text-white mt-16">
          <h2 className="text-2xl font-bold mb-4">{t('cta.title')}</h2>
          <p className="text-primary-100 mb-6 max-w-2xl mx-auto">
            {t('cta.subtitle')}
          </p>
          <div className="flex flex-col sm:flex-row gap-4 justify-center">
            <Link href="/search">
              <Button variant="secondary" size="lg">
                {t('cta.startFree')}
              </Button>
            </Link>
            <Button variant="outline" size="lg" className="border-white text-white hover:bg-white hover:text-primary-600">
              {t('cta.viewDemo')}
            </Button>
          </div>
        </div>
      </div>
    </div>
  );
}