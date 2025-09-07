'use client';

import React, { useState } from 'react';
import Link from 'next/link';
import { ChevronDown, ChevronUp, HelpCircle, Search, MessageSquare } from 'lucide-react';
import { Button } from '../../components/ui/Button';
import { Input } from '../../components/ui/Input';

interface FAQ {
  id: string;
  question: string;
  answer: string;
  category: string;
}

export default function FAQPage() {
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('all');
  const [openItems, setOpenItems] = useState<Set<string>>(new Set());

  const faqs: FAQ[] = [
    // General
    {
      id: '1',
      category: 'general',
      question: 'Wat is Buurtinzicht?',
      answer: 'Buurtinzicht is een platform dat je helpt bij het vinden van de ideale buurt om te wonen in België. We combineren officiële data met je persoonlijke voorkeuren om gepersonaliseerde aanbevelingen te geven.'
    },
    {
      id: '2',
      category: 'general',
      question: 'Hoe werkt de buurt-matching?',
      answer: 'Je geeft aan wat belangrijk is voor jou (veiligheid, transport, cultuur, etc.) en wij berekenen scores voor alle buurten op basis van jouw voorkeuren. Zo vind je buurten die het beste bij je levensstijl passen.'
    },
    {
      id: '3',
      category: 'general',
      question: 'Welke gebieden dekken jullie?',
      answer: 'We dekken heel België met gedetailleerde data voor meer dan 11.000 buurten en wijken. Van grote steden tot kleine dorpen - alle gemeenten zijn opgenomen in onze database.'
    },
    
    // Data
    {
      id: '4',
      category: 'data',
      question: 'Hoe nauwkeurig is jullie data?',
      answer: 'Onze data komt van betrouwbare bronnen zoals Statbel, FOD Binnenlandse Zaken, MIVB/De Lijn/TEC en andere officiële instanties. We updaten de informatie regelmatig om de nauwkeurigheid te garanderen.'
    },
    {
      id: '5',
      category: 'data',
      question: 'Hoe vaak wordt de data bijgewerkt?',
      answer: 'Basis demografische data wordt jaarlijks bijgewerkt. Informatie over transport, criminaliteit en voorzieningen wordt maandelijks geüpdatet. Prijsgegevens worden wekelijks bijgewerkt.'
    },
    {
      id: '6',
      category: 'data',
      question: 'Kan ik zelf data toevoegen of corrigeren?',
      answer: 'Momenteel kunnen gebruikers geen data direct wijzigen. Wel kun je via ons contactformulier fouten melden of suggesties doen. We controleren alle meldingen en passen indien nodig de data aan.'
    },
    
    // Features
    {
      id: '7',
      category: 'features',
      question: 'Kan ik mijn voorkeuren aanpassen?',
      answer: 'Ja, je kunt altijd je voorkeuren wijzigen in je account instellingen. Je scorecards worden automatisch herberekend op basis van je nieuwe voorkeuren.'
    },
    {
      id: '8',
      category: 'features',
      question: 'Wat is een scorecard?',
      answer: 'Een scorecard is een gepersonaliseerd rapport dat laat zien hoe goed een buurt bij jou past. Het bevat scores voor verschillende categorieën en vergelijkt de buurt met je ideale profiel.'
    },
    {
      id: '9',
      category: 'features',
      question: 'Kan ik buurten vergelijken?',
      answer: 'Ja, je kunt tot 4 buurten tegelijk vergelijken. Dit helpt je bij het maken van een weloverwogen keuze tussen verschillende opties.'
    },
    {
      id: '10',
      category: 'features',
      question: 'Kunnen meerdere personen een account delen?',
      answer: 'Een account is bedoeld voor individueel gebruik. Voor families raden we aan om samen de voorkeuren in te stellen of elk een eigen account aan te maken.'
    },
    
    // Pricing
    {
      id: '11',
      category: 'pricing',
      question: 'Wat kost Buurtinzicht?',
      answer: 'We hebben een gratis plan met basisfeatures en betaalde plannen vanaf €19/maand. Bekijk onze prijzenpagina voor een volledig overzicht van features per plan.'
    },
    {
      id: '12',
      category: 'pricing',
      question: 'Is er een gratis proefperiode?',
      answer: 'Het gratis plan geeft je al toegang tot de kernfuncties. Voor betaalde plannen bieden we een 14-dagen geld-terug-garantie als je niet tevreden bent.'
    },
    {
      id: '13',
      category: 'pricing',
      question: 'Kan ik mijn abonnement opzeggen?',
      answer: 'Ja, je kunt je abonnement op elk moment opzeggen. Er zijn geen langetermijncontracten. Je behoudt toegang tot betaalde features tot het einde van je factureringsperiode.'
    },
    {
      id: '14',
      category: 'pricing',
      question: 'Welke betaalmethoden accepteren jullie?',
      answer: 'We accepteren alle grote creditcards (Visa, Mastercard, American Express), SEPA overschrijvingen en PayPal. Alle betalingen worden veilig verwerkt.'
    },
    
    // Technical
    {
      id: '15',
      category: 'technical',
      question: 'Is er een mobiele app?',
      answer: 'Momenteel hebben we geen native app, maar onze website is volledig geoptimaliseerd voor mobiel gebruik en werkt uitstekend op smartphones en tablets.'
    },
    {
      id: '16',
      category: 'technical',
      question: 'Werkt Buurtinzicht op alle browsers?',
      answer: 'Ja, we ondersteunen alle moderne browsers: Chrome, Firefox, Safari, Edge. Voor de beste ervaring raden we aan om je browser up-to-date te houden.'
    },
    {
      id: '17',
      category: 'technical',
      question: 'Is er een API beschikbaar?',
      answer: 'Ja, we bieden een REST API voor zakelijke gebruikers. Contact ons voor meer informatie over API toegang en pricing.'
    }
  ];

  const categories = [
    { value: 'all', label: 'Alle categorieën' },
    { value: 'general', label: 'Algemeen' },
    { value: 'data', label: 'Data & Nauwkeurigheid' },
    { value: 'features', label: 'Features & Gebruik' },
    { value: 'pricing', label: 'Prijzen & Abonnementen' },
    { value: 'technical', label: 'Technisch' }
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
              <h1 className="text-4xl font-bold text-neutral-900">Veelgestelde Vragen</h1>
            </div>
            <p className="text-lg text-neutral-600 max-w-3xl mx-auto">
              Vind snel antwoorden op de meest gestelde vragen over Buurtinzicht
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
                  placeholder="Zoek in de FAQ..."
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
              Geen resultaten gevonden
            </h2>
            <p className="text-neutral-600 mb-6">
              Probeer een andere zoekterm of wijzig je categorie
            </p>
            <div className="flex justify-center gap-4">
              <Button variant="outline" onClick={() => setSearchTerm('')}>
                Wis zoekterm
              </Button>
              <Button variant="outline" onClick={() => setSelectedCategory('all')}>
                Alle categorieën
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
            <div className="text-sm text-neutral-600">FAQ artikelen</div>
          </div>
          <div className="text-center p-6 bg-white rounded-lg shadow-sm border border-neutral-200">
            <Search className="h-8 w-8 text-primary-600 mx-auto mb-2" />
            <div className="text-2xl font-bold text-neutral-900">{categories.length - 1}</div>
            <div className="text-sm text-neutral-600">Categorieën</div>
          </div>
          <div className="text-center p-6 bg-white rounded-lg shadow-sm border border-neutral-200">
            <MessageSquare className="h-8 w-8 text-primary-600 mx-auto mb-2" />
            <div className="text-2xl font-bold text-neutral-900">24h</div>
            <div className="text-sm text-neutral-600">Reactietijd</div>
          </div>
        </div>

        {/* Contact CTA */}
        <div className="bg-primary-600 rounded-lg text-white p-8 text-center mt-12">
          <h2 className="text-2xl font-bold mb-4">Vraag niet beantwoord?</h2>
          <p className="text-primary-100 mb-6">
            Ons support team helpt je graag verder met je specifieke vraag
          </p>
          <div className="flex flex-col sm:flex-row gap-4 justify-center">
            <Link href="/contact">
              <Button variant="secondary" size="lg">
                <MessageSquare className="h-4 w-4 mr-2" />
                Neem contact op
              </Button>
            </Link>
            <Button variant="outline" size="lg" className="border-white text-white hover:bg-white hover:text-primary-600">
              <HelpCircle className="h-4 w-4 mr-2" />
              Live chat starten
            </Button>
          </div>
        </div>
      </div>
    </div>
  );
}