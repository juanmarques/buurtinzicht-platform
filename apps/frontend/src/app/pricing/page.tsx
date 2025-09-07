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
  const [billingCycle, setBillingCycle] = useState<'monthly' | 'yearly'>('monthly');

  const plans: PricingPlan[] = [
    {
      id: 'free',
      name: 'Gratis',
      description: 'Perfect om te beginnen met het ontdekken van buurten',
      price: {
        monthly: 0,
        yearly: 0
      },
      features: [
        { name: '5 zoekopdrachten per maand', included: true },
        { name: '2 buurt vergelijkingen per maand', included: true },
        { name: 'Basis buurtinformatie', included: true },
        { name: 'Openbare kaarten', included: true },
        { name: 'Tot 3 favoriete buurten', included: true },
        { name: 'Gedetailleerde scorecards', included: false },
        { name: 'Onbeperkte zoekopdrachten', included: false },
        { name: 'Premium data en insights', included: false },
        { name: 'API toegang', included: false },
        { name: 'Prioriteit ondersteuning', included: false }
      ],
      highlighted: false,
      icon: Heart,
      color: 'text-neutral-600'
    },
    {
      id: 'pro',
      name: 'Pro',
      description: 'Ideaal voor huiskopers en makelaars die serieus zoeken',
      price: {
        monthly: 19,
        yearly: 190
      },
      features: [
        { name: 'Onbeperkte zoekopdrachten', included: true },
        { name: 'Onbeperkte vergelijkingen', included: true },
        { name: 'Gedetailleerde scorecards', included: true },
        { name: 'Premium kaarten en visualisaties', included: true },
        { name: 'Tot 50 favoriete buurten', included: true },
        { name: 'Historische data en trends', included: true },
        { name: 'Gepersonaliseerde aanbevelingen', included: true },
        { name: 'Export functionaliteit', included: true },
        { name: 'API toegang (beperkt)', included: false },
        { name: 'Prioriteit ondersteuning', included: false }
      ],
      highlighted: true,
      icon: Star,
      color: 'text-primary-600'
    },
    {
      id: 'business',
      name: 'Business',
      description: 'Voor vastgoedprofessionals en bedrijven met hoogwaardige behoeften',
      price: {
        monthly: 49,
        yearly: 490
      },
      features: [
        { name: 'Alles uit Pro plan', included: true },
        { name: 'Volledige API toegang', included: true },
        { name: 'Bulk data export', included: true },
        { name: 'Aangepaste rapporten', included: true },
        { name: 'Team collaboration tools', included: true },
        { name: 'White-label opties', included: true },
        { name: 'Prioriteit ondersteuning', included: true },
        { name: 'Training en onboarding', included: true },
        { name: 'Custom integraties', included: true },
        { name: 'SLA garanties', included: true }
      ],
      highlighted: false,
      icon: Crown,
      color: 'text-yellow-600'
    }
  ];

  const featuresComparison = [
    {
      category: 'Zoeken & Ontdekken',
      features: [
        { name: 'Zoekopdrachten per maand', free: '5', pro: 'Onbeperkt', business: 'Onbeperkt' },
        { name: 'Geavanceerde filters', free: 'Basis', pro: 'Volledig', business: 'Volledig + Custom' },
        { name: 'Radius zoeken', free: '✓', pro: '✓', business: '✓' },
        { name: 'Locatie gebaseerd zoeken', free: '✓', pro: '✓', business: '✓' }
      ]
    },
    {
      category: 'Data & Insights',
      features: [
        { name: 'Basis buurtinformatie', free: '✓', pro: '✓', business: '✓' },
        { name: 'Gedetailleerde scorecards', free: '✗', pro: '✓', business: '✓' },
        { name: 'Historische trends', free: '✗', pro: '✓', business: '✓' },
        { name: 'Voorspellende analytics', free: '✗', pro: '✗', business: '✓' }
      ]
    },
    {
      category: 'Tools & Features',
      features: [
        { name: 'Buurt vergelijkingen', free: '2/maand', pro: 'Onbeperkt', business: 'Onbeperkt' },
        { name: 'Favorieten opslaan', free: '3', pro: '50', business: 'Onbeperkt' },
        { name: 'Export functionaliteit', free: '✗', pro: 'PDF/CSV', business: 'Alle formaten' },
        { name: 'Team samenwerking', free: '✗', pro: '✗', business: '✓' }
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
              Kies het juiste plan voor jou
            </h1>
            <p className="text-xl text-neutral-600 max-w-3xl mx-auto mb-8">
              Begin gratis of upgrade naar een betaald plan voor toegang tot premium functies en onbeperkte mogelijkheden
            </p>
            
            {/* Billing Toggle */}
            <div className="flex items-center justify-center mb-8">
              <span className={`mr-3 ${billingCycle === 'monthly' ? 'text-neutral-900 font-medium' : 'text-neutral-600'}`}>
                Maandelijks
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
                Jaarlijks
              </span>
              {billingCycle === 'yearly' && (
                <span className="ml-2 bg-green-100 text-green-800 text-sm px-2 py-1 rounded-full">
                  Bespaar tot 17%
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
                      Meest populair
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
                        €{getMonthlyPrice(plan).toFixed(0)}
                        {plan.price.monthly > 0 && <span className="text-lg text-neutral-600">/maand</span>}
                      </div>
                      {billingCycle === 'yearly' && plan.price.monthly > 0 && (
                        <div className="text-sm text-neutral-600">
                          €{getPrice(plan)} per jaar • Bespaar {getSavings(plan)}%
                        </div>
                      )}
                    </div>
                    
                    <Button
                      variant={plan.highlighted ? 'primary' : 'outline'}
                      className="w-full"
                      size="lg"
                    >
                      {plan.id === 'free' ? 'Begin gratis' : 'Start vandaag'}
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
            <h2 className="text-2xl font-bold text-neutral-900 mb-2">Gedetailleerde vergelijking</h2>
            <p className="text-neutral-600">Vergelijk alle functies naast elkaar om de juiste keuze te maken</p>
          </div>
          
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead className="bg-neutral-50">
                <tr>
                  <th className="px-8 py-4 text-left text-sm font-medium text-neutral-900">Functies</th>
                  <th className="px-8 py-4 text-center text-sm font-medium text-neutral-900">Gratis</th>
                  <th className="px-8 py-4 text-center text-sm font-medium text-neutral-900">Pro</th>
                  <th className="px-8 py-4 text-center text-sm font-medium text-neutral-900">Business</th>
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
          <h2 className="text-2xl font-bold text-neutral-900 text-center mb-8">Veelgestelde vragen</h2>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
            <div>
              <h3 className="font-semibold text-neutral-900 mb-2">Kan ik mijn plan op elk moment wijzigen?</h3>
              <p className="text-neutral-600 text-sm">
                Ja, je kunt je plan op elk moment upgraden of downgraden. Wijzigingen gaan meteen in en je wordt pro-rata gefactureerd.
              </p>
            </div>
            <div>
              <h3 className="font-semibold text-neutral-900 mb-2">Is er een gratis proefperiode?</h3>
              <p className="text-neutral-600 text-sm">
                Het gratis plan geeft je al toegang tot de kernfuncties. Voor betaalde plannen bieden we een 14-dagen geld-terug-garantie.
              </p>
            </div>
            <div>
              <h3 className="font-semibold text-neutral-900 mb-2">Welke betaalmethoden accepteren jullie?</h3>
              <p className="text-neutral-600 text-sm">
                We accepteren alle grote creditcards, SEPA overschrijving en PayPal voor een veilige en gemakkelijke betaalervaring.
              </p>
            </div>
            <div>
              <h3 className="font-semibold text-neutral-900 mb-2">Is mijn data veilig?</h3>
              <p className="text-neutral-600 text-sm">
                Absoluut. We gebruiken enterprise-grade beveiliging en voldoen aan alle GDPR-richtlijnen om je data te beschermen.
              </p>
            </div>
          </div>
        </div>

        {/* CTA */}
        <div className="bg-primary-600 rounded-2xl p-8 text-center text-white mt-16">
          <h2 className="text-2xl font-bold mb-4">Klaar om je ideale buurt te vinden?</h2>
          <p className="text-primary-100 mb-6 max-w-2xl mx-auto">
            Sluit je aan bij duizenden mensen die al hun perfecte buurt hebben gevonden met Buurtinzicht
          </p>
          <div className="flex flex-col sm:flex-row gap-4 justify-center">
            <Link href="/search">
              <Button variant="secondary" size="lg">
                Begin gratis
              </Button>
            </Link>
            <Button variant="outline" size="lg" className="border-white text-white hover:bg-white hover:text-primary-600">
              Bekijk demo
            </Button>
          </div>
        </div>
      </div>
    </div>
  );
}