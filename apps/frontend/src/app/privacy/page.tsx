'use client';

import React from 'react';
import { Shield, Eye, Lock, Users, FileText, Mail, Calendar } from 'lucide-react';

export default function PrivacyPage() {
  const lastUpdated = '20 januari 2024';

  const sections = [
    {
      title: 'Welke informatie verzamelen wij?',
      icon: FileText,
      content: [
        'Accountinformatie: naam, e-mailadres, wachtwoord (gehashed), voorkeuren',
        'Zoekgeschiedenis: zoekopdrachten, bekeken buurten, favorieten',
        'Technische gegevens: IP-adres, browsertype, apparaatinformatie',
        'Gebruiksstatistieken: hoe je de website gebruikt (geanonimiseerd)',
        'Cookies: voor sessies, voorkeuren en analytische doeleinden'
      ]
    },
    {
      title: 'Hoe gebruiken wij je gegevens?',
      icon: Eye,
      content: [
        'Gepersonaliseerde buurtaanbevelingen op basis van je voorkeuren',
        'Het bijhouden van je zoekgeschiedenis en favorieten',
        'Verbeteren van onze dienstverlening en gebruikerservaring',
        'Communicatie over account updates en nieuwe functies',
        'Technische ondersteuning en klantenservice',
        'Naleving van wettelijke verplichtingen'
      ]
    },
    {
      title: 'Delen wij je gegevens?',
      icon: Users,
      content: [
        'Wij verkopen nooit persoonlijke gegevens aan derden',
        'Geanonimiseerde data kan gedeeld worden voor onderzoek',
        'Serviceproviders (hosting, analytics) onder strenge contracten',
        'Wettelijk verplichte openbaarmaking indien nodig',
        'Bedrijfsovername: gegevens kunnen onderdeel zijn van transactie'
      ]
    },
    {
      title: 'Hoe beveiligen wij je gegevens?',
      icon: Lock,
      content: [
        'SSL/TLS encryptie voor alle gegevensoverdracht',
        'Wachtwoorden worden veilig gehashed opgeslagen',
        'Regelmatige beveiligingsaudits en updates',
        'Toegang tot gegevens is beperkt tot geautoriseerd personeel',
        'Backups worden versleuteld opgeslagen',
        'Compliance met GDPR en andere privacyregels'
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
              <h1 className="text-4xl font-bold text-neutral-900">Privacybeleid</h1>
            </div>
            <p className="text-lg text-neutral-600 max-w-3xl mx-auto">
              Bij Buurtinzicht nemen we je privacy serieus. Dit beleid legt uit hoe we je 
              persoonlijke gegevens verzamelen, gebruiken en beschermen.
            </p>
            <div className="flex items-center justify-center mt-6 text-sm text-neutral-500">
              <Calendar className="h-4 w-4 mr-2" />
              Laatst bijgewerkt: {lastUpdated}
            </div>
          </div>
        </div>
      </div>

      <div className="mx-auto max-w-4xl px-4 sm:px-6 lg:px-8 py-16">
        {/* Introduction */}
        <div className="bg-primary-50 border border-primary-200 rounded-lg p-6 mb-12">
          <h2 className="text-xl font-semibold text-primary-900 mb-3">In het kort</h2>
          <p className="text-primary-800 mb-4">
            Buurtinzicht BV ("wij", "ons") respecteert je privacy en is committed aan het beschermen 
            van je persoonlijke gegevens. Dit privacybeleid is van toepassing op onze website en diensten.
          </p>
          <ul className="text-primary-800 space-y-1 text-sm">
            <li>• We verzamelen alleen gegevens die nodig zijn voor onze dienstverlening</li>
            <li>• Je hebt altijd controle over je eigen gegevens</li>
            <li>• We delen nooit persoonlijke gegevens voor commerciële doeleinden</li>
            <li>• Alle gegevens worden veilig opgeslagen volgens GDPR-standaarden</li>
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
            <h2 className="text-2xl font-semibold text-neutral-900">Je rechten (GDPR)</h2>
          </div>
          
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <h3 className="font-semibold text-neutral-900 mb-3">Toegang & Controle</h3>
              <ul className="space-y-2 text-neutral-700">
                <li className="flex items-start">
                  <Eye className="h-4 w-4 text-green-600 mr-2 mt-0.5 flex-shrink-0" />
                  <span>Inzage in je opgeslagen gegevens</span>
                </li>
                <li className="flex items-start">
                  <FileText className="h-4 w-4 text-green-600 mr-2 mt-0.5 flex-shrink-0" />
                  <span>Correctie van onjuiste informatie</span>
                </li>
                <li className="flex items-start">
                  <Lock className="h-4 w-4 text-green-600 mr-2 mt-0.5 flex-shrink-0" />
                  <span>Beperking van gegevensverwerking</span>
                </li>
              </ul>
            </div>
            <div>
              <h3 className="font-semibold text-neutral-900 mb-3">Verwijdering & Overdracht</h3>
              <ul className="space-y-2 text-neutral-700">
                <li className="flex items-start">
                  <Users className="h-4 w-4 text-green-600 mr-2 mt-0.5 flex-shrink-0" />
                  <span>Overdracht naar andere diensten</span>
                </li>
                <li className="flex items-start">
                  <Shield className="h-4 w-4 text-green-600 mr-2 mt-0.5 flex-shrink-0" />
                  <span>Volledige verwijdering van je account</span>
                </li>
                <li className="flex items-start">
                  <Mail className="h-4 w-4 text-green-600 mr-2 mt-0.5 flex-shrink-0" />
                  <span>Bezwaar tegen direct marketing</span>
                </li>
              </ul>
            </div>
          </div>
          
          <div className="bg-neutral-50 rounded-lg p-4 mt-6">
            <p className="text-sm text-neutral-600">
              <strong>Uitoefenen van je rechten:</strong> Stuur een e-mail naar{' '}
              <a href="mailto:privacy@buurtinzicht.be" className="text-primary-600 hover:text-primary-700">
                privacy@buurtinzicht.be
              </a>{' '}
              met je verzoek. We reageren binnen 30 dagen.
            </p>
          </div>
        </div>

        {/* Cookies */}
        <div className="bg-white rounded-lg shadow-sm border border-neutral-200 p-8 mt-12">
          <h2 className="text-2xl font-semibold text-neutral-900 mb-6">Cookies & Tracking</h2>
          
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-6">
            <div className="bg-green-50 border border-green-200 rounded-lg p-4">
              <h3 className="font-semibold text-green-900 mb-2">Essentiële Cookies</h3>
              <p className="text-sm text-green-700">
                Noodzakelijk voor het functioneren van de website (login, winkelwagen, voorkeuren)
              </p>
            </div>
            <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
              <h3 className="font-semibold text-blue-900 mb-2">Analytische Cookies</h3>
              <p className="text-sm text-blue-700">
                Helpen ons begrijpen hoe gebruikers de website gebruiken (geanonimiseerd)
              </p>
            </div>
            <div className="bg-purple-50 border border-purple-200 rounded-lg p-4">
              <h3 className="font-semibold text-purple-900 mb-2">Marketing Cookies</h3>
              <p className="text-sm text-purple-700">
                Voor gepersonaliseerde advertenties (alleen met je toestemming)
              </p>
            </div>
          </div>
          
          <p className="text-neutral-600 text-sm">
            Je kunt je cookie-voorkeuren aanpassen in je{' '}
            <a href="/dashboard/settings" className="text-primary-600 hover:text-primary-700">
              account instellingen
            </a>{' '}
            of via de cookie-banner op onze website.
          </p>
        </div>

        {/* Data Retention */}
        <div className="bg-white rounded-lg shadow-sm border border-neutral-200 p-8 mt-12">
          <h2 className="text-2xl font-semibold text-neutral-900 mb-6">Hoe lang bewaren wij je gegevens?</h2>
          
          <div className="space-y-4">
            <div className="flex justify-between items-center py-3 border-b border-neutral-200">
              <span className="font-medium text-neutral-900">Account gegevens</span>
              <span className="text-neutral-600">Totdat je je account verwijdert</span>
            </div>
            <div className="flex justify-between items-center py-3 border-b border-neutral-200">
              <span className="font-medium text-neutral-900">Zoekgeschiedenis</span>
              <span className="text-neutral-600">2 jaar na laatste activiteit</span>
            </div>
            <div className="flex justify-between items-center py-3 border-b border-neutral-200">
              <span className="font-medium text-neutral-900">Technische logs</span>
              <span className="text-neutral-600">30 dagen</span>
            </div>
            <div className="flex justify-between items-center py-3 border-b border-neutral-200">
              <span className="font-medium text-neutral-900">Marketing gegevens</span>
              <span className="text-neutral-600">Tot je je uitschrijft</span>
            </div>
            <div className="flex justify-between items-center py-3">
              <span className="font-medium text-neutral-900">Geanonimiseerde statistieken</span>
              <span className="text-neutral-600">Permanent (kan niet herleid worden)</span>
            </div>
          </div>
        </div>

        {/* Contact Information */}
        <div className="bg-primary-600 rounded-lg text-white p-8 mt-12">
          <h2 className="text-2xl font-semibold mb-4">Vragen over privacy?</h2>
          <p className="text-primary-100 mb-6">
            Heb je vragen over dit privacybeleid of hoe we omgaan met je gegevens? 
            Neem gerust contact op met ons privacy team.
          </p>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <h3 className="font-semibold mb-2">Data Protection Officer</h3>
              <p className="text-primary-100">
                <Mail className="inline h-4 w-4 mr-1" />
                privacy@buurtinzicht.be
              </p>
            </div>
            <div>
              <h3 className="font-semibold mb-2">Algemene vragen</h3>
              <p className="text-primary-100">
                <Mail className="inline h-4 w-4 mr-1" />
                info@buurtinzicht.be
              </p>
            </div>
          </div>
          <div className="bg-primary-700 rounded-lg p-4 mt-6">
            <p className="text-sm text-primary-100">
              <strong>Klacht indienen:</strong> Je kunt ook een klacht indienen bij de Belgische 
              Gegevensbeschermingsautoriteit (GBA) via{' '}
              <a href="https://www.gegevensbeschermingsautoriteit.be" className="text-white hover:underline">
                hun website
              </a>.
            </p>
          </div>
        </div>

        {/* Updates */}
        <div className="bg-neutral-100 rounded-lg p-6 mt-12">
          <h2 className="text-lg font-semibold text-neutral-900 mb-3">Wijzigingen in dit beleid</h2>
          <p className="text-neutral-700 text-sm">
            We kunnen dit privacybeleid van tijd tot tijd bijwerken. Belangrijke wijzigingen 
            communiceren we via e-mail of een melding op onze website. De datum van de laatste 
            wijziging staat altijd bovenaan deze pagina. Door gebruik te blijven maken van onze 
            diensten na wijzigingen, ga je akkoord met het bijgewerkte beleid.
          </p>
        </div>
      </div>
    </div>
  );
}