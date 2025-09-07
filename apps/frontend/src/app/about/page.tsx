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

export default function AboutPage() {
  const values = [
    {
      icon: Target,
      title: 'Precisie',
      description: 'We leveren nauwkeurige en betrouwbare data om je te helpen weloverwogen beslissingen te nemen.'
    },
    {
      icon: Heart,
      title: 'Gebruikersgericht',
      description: 'Jouw behoeften staan centraal. We bouwen tools die echt helpen bij het vinden van je ideale thuis.'
    },
    {
      icon: Zap,
      title: 'Innovatie',
      description: 'We gebruiken de nieuwste technologieën om complexe data om te zetten naar begrijpelijke inzichten.'
    },
    {
      icon: Shield,
      title: 'Transparantie',
      description: 'Openheid over onze methodiek en databronnen, zodat je kunt vertrouwen op onze analyses.'
    }
  ];

  const team = [
    {
      name: 'Sarah De Vries',
      role: 'CEO & Oprichter',
      bio: 'Expert in ruimtelijke data-analyse met 10+ jaar ervaring in de vastgoedsector.',
      avatar: '👩‍💼'
    },
    {
      name: 'Tom Janssens',
      role: 'CTO',
      bio: 'Technologie-expert gespecialiseerd in GIS-systemen en machine learning.',
      avatar: '👨‍💻'
    },
    {
      name: 'Lisa Chen',
      role: 'Lead Data Scientist',
      bio: 'PhD in Stedelijke Planning met focus op demografische trends en leefbaarheid.',
      avatar: '👩‍🔬'
    },
    {
      name: 'Marc Dubois',
      role: 'Product Manager',
      bio: '15 jaar ervaring in het ontwikkelen van gebruiksvriendelijke data-platforms.',
      avatar: '👨‍💼'
    }
  ];

  const stats = [
    { number: '50.000+', label: 'Tevreden gebruikers' },
    { number: '11.000+', label: 'Belgische buurten' },
    { number: '1M+', label: 'Zoekopdrachten verwerkt' },
    { number: '99.9%', label: 'Beschikbaarheid' }
  ];

  return (
    <div className="min-h-screen bg-neutral-50">
      {/* Hero Section */}
      <div className="bg-white border-b border-neutral-200">
        <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8 py-16">
          <div className="text-center">
            <h1 className="text-4xl sm:text-5xl font-bold text-neutral-900 mb-6">
              Over Buurtinzicht
            </h1>
            <p className="text-xl text-neutral-600 max-w-3xl mx-auto mb-8">
              Wij helpen mensen hun ideale buurt te vinden door complexe data om te zetten naar 
              begrijpelijke inzichten en gepersonaliseerde aanbevelingen.
            </p>
            <div className="flex items-center justify-center space-x-8">
              <div className="flex items-center">
                <MapPin className="h-8 w-8 text-primary-600 mr-2" />
                <span className="text-lg font-medium text-neutral-900">Opgericht in 2020</span>
              </div>
              <div className="flex items-center">
                <Globe className="h-8 w-8 text-primary-600 mr-2" />
                <span className="text-lg font-medium text-neutral-900">Brussel, België</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Mission Section */}
      <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8 py-16">
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-16 items-center">
          <div>
            <h2 className="text-3xl font-bold text-neutral-900 mb-6">Onze Missie</h2>
            <p className="text-lg text-neutral-600 mb-6">
              Het vinden van de perfecte buurt om te wonen is een van de belangrijkste beslissingen 
              in iemands leven. Toch is het vaak moeilijk om objectieve informatie te vinden die 
              aansluit bij jouw persoonlijke behoeften en voorkeuren.
            </p>
            <p className="text-lg text-neutral-600 mb-6">
              Daarom hebben we Buurtinzicht ontwikkeld: een platform dat uitgebreide data over 
              Belgische buurten combineert met jouw persoonlijke voorkeuren om de beste match te vinden.
            </p>
            <Link href="/search">
              <Button variant="primary" size="lg">
                Ontdek je ideale buurt
              </Button>
            </Link>
          </div>
          <div className="bg-gradient-to-r from-primary-50 to-blue-50 rounded-2xl p-8">
            <h3 className="text-xl font-semibold text-neutral-900 mb-4">Wat maakt ons uniek?</h3>
            <ul className="space-y-3">
              <li className="flex items-start">
                <Award className="h-5 w-5 text-primary-600 mr-3 mt-0.5 flex-shrink-0" />
                <span className="text-neutral-700">Gepersonaliseerde scorecards gebaseerd op jouw voorkeuren</span>
              </li>
              <li className="flex items-start">
                <Users className="h-5 w-5 text-primary-600 mr-3 mt-0.5 flex-shrink-0" />
                <span className="text-neutral-700">Realtime data van betrouwbare overheids- en commerciële bronnen</span>
              </li>
              <li className="flex items-start">
                <MapPin className="h-5 w-5 text-primary-600 mr-3 mt-0.5 flex-shrink-0" />
                <span className="text-neutral-700">Interactieve kaarten met gedetailleerde buurtinformatie</span>
              </li>
              <li className="flex items-start">
                <Zap className="h-5 w-5 text-primary-600 mr-3 mt-0.5 flex-shrink-0" />
                <span className="text-neutral-700">AI-gedreven aanbevelingen voor vergelijkbare buurten</span>
              </li>
            </ul>
          </div>
        </div>
      </div>

      {/* Values Section */}
      <div className="bg-white">
        <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8 py-16">
          <div className="text-center mb-12">
            <h2 className="text-3xl font-bold text-neutral-900 mb-4">Onze Waarden</h2>
            <p className="text-lg text-neutral-600 max-w-2xl mx-auto">
              Deze kernwaarden sturen alles wat we doen, van productontwerp tot klantenservice.
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
          <h2 className="text-3xl font-bold text-neutral-900 mb-4">Buurtinzicht in cijfers</h2>
          <p className="text-lg text-neutral-600">
            Onze impact tot nu toe
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
            <h2 className="text-3xl font-bold text-neutral-900 mb-4">Ons Team</h2>
            <p className="text-lg text-neutral-600 max-w-2xl mx-auto">
              Ontmoet de experts die Buurtinzicht mogelijk maken
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
            <h2 className="text-3xl font-bold mb-4">Klaar om je ideale buurt te vinden?</h2>
            <p className="text-xl text-primary-100 mb-8 max-w-2xl mx-auto">
              Sluit je aan bij duizenden tevreden gebruikers die al hun perfecte thuis hebben gevonden.
            </p>
            <div className="flex flex-col sm:flex-row gap-4 justify-center">
              <Link href="/search">
                <Button variant="secondary" size="lg">
                  Begin je zoektocht
                </Button>
              </Link>
              <Link href="/contact">
                <Button variant="outline" size="lg" className="border-white text-white hover:bg-white hover:text-primary-600">
                  Neem contact op
                </Button>
              </Link>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}