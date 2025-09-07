'use client';

import React, { useState } from 'react';
import Link from 'next/link';
import { ArrowLeft, User, Mail, Calendar, Shield, Bell, Eye, Save } from 'lucide-react';
import { Button } from '../../../components/ui/Button';
import { Input } from '../../../components/ui/Input';

export default function ProfilePage() {
  const [profile, setProfile] = useState({
    firstName: 'Jan',
    lastName: 'Janssen',
    email: 'jan.janssen@example.com',
    phone: '+32 123 456 789',
    dateOfBirth: '1990-05-15',
    language: 'nl',
    newsletter: true,
    searchAlerts: true,
    marketingEmails: false,
  });

  const [isEditing, setIsEditing] = useState(false);
  const [isSaving, setIsSaving] = useState(false);

  const handleSave = async () => {
    setIsSaving(true);
    // Simulate API call
    await new Promise(resolve => setTimeout(resolve, 1000));
    setIsSaving(false);
    setIsEditing(false);
  };

  const handleInputChange = (field: string, value: string | boolean) => {
    setProfile(prev => ({ ...prev, [field]: value }));
  };

  return (
    <div className="min-h-screen bg-neutral-50">
      {/* Header */}
      <div className="bg-white border-b border-neutral-200">
        <div className="mx-auto max-w-4xl px-4 sm:px-6 lg:px-8 py-6">
          <div className="flex items-center">
            <Link href="/dashboard">
              <Button variant="ghost" icon={ArrowLeft} className="mr-4">
                Terug naar dashboard
              </Button>
            </Link>
            <div>
              <h1 className="text-3xl font-bold text-neutral-900">Profiel instellingen</h1>
              <p className="text-neutral-600 mt-1">Beheer je persoonlijke informatie en voorkeuren</p>
            </div>
          </div>
        </div>
      </div>

      <div className="mx-auto max-w-4xl px-4 sm:px-6 lg:px-8 py-8">
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Navigation */}
          <div className="lg:col-span-1">
            <div className="bg-white rounded-lg shadow-sm border border-neutral-200 p-4">
              <nav className="space-y-2">
                <a 
                  href="#personal" 
                  className="flex items-center px-3 py-2 text-sm font-medium text-primary-700 bg-primary-50 rounded-lg"
                >
                  <User className="h-4 w-4 mr-3" />
                  Persoonlijke gegevens
                </a>
                <a 
                  href="#notifications" 
                  className="flex items-center px-3 py-2 text-sm font-medium text-neutral-700 hover:bg-neutral-50 rounded-lg"
                >
                  <Bell className="h-4 w-4 mr-3" />
                  Notificaties
                </a>
                <a 
                  href="#privacy" 
                  className="flex items-center px-3 py-2 text-sm font-medium text-neutral-700 hover:bg-neutral-50 rounded-lg"
                >
                  <Shield className="h-4 w-4 mr-3" />
                  Privacy & Beveiliging
                </a>
              </nav>
            </div>
          </div>

          {/* Main Content */}
          <div className="lg:col-span-2 space-y-8">
            {/* Personal Information */}
            <div id="personal" className="bg-white rounded-lg shadow-sm border border-neutral-200">
              <div className="p-6 border-b border-neutral-200">
                <div className="flex items-center justify-between">
                  <h2 className="text-lg font-semibold text-neutral-900 flex items-center">
                    <User className="h-5 w-5 mr-2 text-primary-600" />
                    Persoonlijke gegevens
                  </h2>
                  {!isEditing ? (
                    <Button variant="outline" size="sm" onClick={() => setIsEditing(true)}>
                      Bewerken
                    </Button>
                  ) : (
                    <div className="flex space-x-2">
                      <Button 
                        variant="ghost" 
                        size="sm" 
                        onClick={() => setIsEditing(false)}
                        disabled={isSaving}
                      >
                        Annuleren
                      </Button>
                      <Button 
                        variant="primary" 
                        size="sm" 
                        icon={Save}
                        onClick={handleSave}
                        disabled={isSaving}
                      >
                        {isSaving ? 'Opslaan...' : 'Opslaan'}
                      </Button>
                    </div>
                  )}
                </div>
              </div>
              <div className="p-6 space-y-6">
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
                  <Input
                    label="Voornaam"
                    value={profile.firstName}
                    onChange={(e) => handleInputChange('firstName', e.target.value)}
                    disabled={!isEditing}
                    required
                  />
                  <Input
                    label="Achternaam"
                    value={profile.lastName}
                    onChange={(e) => handleInputChange('lastName', e.target.value)}
                    disabled={!isEditing}
                    required
                  />
                </div>
                <Input
                  label="E-mailadres"
                  type="email"
                  value={profile.email}
                  onChange={(e) => handleInputChange('email', e.target.value)}
                  disabled={!isEditing}
                  icon={Mail}
                  required
                />
                <Input
                  label="Telefoonnummer"
                  type="tel"
                  value={profile.phone}
                  onChange={(e) => handleInputChange('phone', e.target.value)}
                  disabled={!isEditing}
                />
                <Input
                  label="Geboortedatum"
                  type="date"
                  value={profile.dateOfBirth}
                  onChange={(e) => handleInputChange('dateOfBirth', e.target.value)}
                  disabled={!isEditing}
                  icon={Calendar}
                />
                <div>
                  <label className="block text-sm font-medium text-neutral-700 mb-1">
                    Voorkeurstaal
                  </label>
                  <select
                    value={profile.language}
                    onChange={(e) => handleInputChange('language', e.target.value)}
                    disabled={!isEditing}
                    className="block w-full rounded-lg border border-neutral-300 shadow-sm px-3 py-2 focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-primary-500 disabled:bg-neutral-50 disabled:text-neutral-500"
                  >
                    <option value="nl">Nederlands</option>
                    <option value="fr">Français</option>
                    <option value="en">English</option>
                  </select>
                </div>
              </div>
            </div>

            {/* Notifications */}
            <div id="notifications" className="bg-white rounded-lg shadow-sm border border-neutral-200">
              <div className="p-6 border-b border-neutral-200">
                <h2 className="text-lg font-semibold text-neutral-900 flex items-center">
                  <Bell className="h-5 w-5 mr-2 text-primary-600" />
                  Notificatie voorkeuren
                </h2>
                <p className="text-sm text-neutral-600 mt-1">
                  Bepaal hoe en wanneer je notificaties wilt ontvangen
                </p>
              </div>
              <div className="p-6 space-y-4">
                <div className="flex items-center justify-between">
                  <div>
                    <h3 className="font-medium text-neutral-900">Nieuwsbrief</h3>
                    <p className="text-sm text-neutral-600">Ontvang maandelijkse updates over nieuwe functies</p>
                  </div>
                  <label className="relative inline-flex items-center cursor-pointer">
                    <input
                      type="checkbox"
                      checked={profile.newsletter}
                      onChange={(e) => handleInputChange('newsletter', e.target.checked)}
                      className="sr-only"
                    />
                    <div className={`w-11 h-6 rounded-full transition-colors ${profile.newsletter ? 'bg-primary-600' : 'bg-neutral-300'}`}>
                      <div className={`w-5 h-5 bg-white rounded-full shadow transform transition-transform ${profile.newsletter ? 'translate-x-5' : 'translate-x-0'} mt-0.5 ml-0.5`} />
                    </div>
                  </label>
                </div>

                <div className="flex items-center justify-between">
                  <div>
                    <h3 className="font-medium text-neutral-900">Zoekwaarschuwingen</h3>
                    <p className="text-sm text-neutral-600">Krijg meldingen over nieuwe buurten die aan je criteria voldoen</p>
                  </div>
                  <label className="relative inline-flex items-center cursor-pointer">
                    <input
                      type="checkbox"
                      checked={profile.searchAlerts}
                      onChange={(e) => handleInputChange('searchAlerts', e.target.checked)}
                      className="sr-only"
                    />
                    <div className={`w-11 h-6 rounded-full transition-colors ${profile.searchAlerts ? 'bg-primary-600' : 'bg-neutral-300'}`}>
                      <div className={`w-5 h-5 bg-white rounded-full shadow transform transition-transform ${profile.searchAlerts ? 'translate-x-5' : 'translate-x-0'} mt-0.5 ml-0.5`} />
                    </div>
                  </label>
                </div>

                <div className="flex items-center justify-between">
                  <div>
                    <h3 className="font-medium text-neutral-900">Marketing e-mails</h3>
                    <p className="text-sm text-neutral-600">Ontvang aanbiedingen en promotionele content</p>
                  </div>
                  <label className="relative inline-flex items-center cursor-pointer">
                    <input
                      type="checkbox"
                      checked={profile.marketingEmails}
                      onChange={(e) => handleInputChange('marketingEmails', e.target.checked)}
                      className="sr-only"
                    />
                    <div className={`w-11 h-6 rounded-full transition-colors ${profile.marketingEmails ? 'bg-primary-600' : 'bg-neutral-300'}`}>
                      <div className={`w-5 h-5 bg-white rounded-full shadow transform transition-transform ${profile.marketingEmails ? 'translate-x-5' : 'translate-x-0'} mt-0.5 ml-0.5`} />
                    </div>
                  </label>
                </div>
              </div>
            </div>

            {/* Privacy & Security */}
            <div id="privacy" className="bg-white rounded-lg shadow-sm border border-neutral-200">
              <div className="p-6 border-b border-neutral-200">
                <h2 className="text-lg font-semibold text-neutral-900 flex items-center">
                  <Shield className="h-5 w-5 mr-2 text-primary-600" />
                  Privacy & Beveiliging
                </h2>
              </div>
              <div className="p-6 space-y-4">
                <div className="flex items-center justify-between">
                  <div>
                    <h3 className="font-medium text-neutral-900">Wachtwoord wijzigen</h3>
                    <p className="text-sm text-neutral-600">Update je wachtwoord om je account veilig te houden</p>
                  </div>
                  <Button variant="outline" size="sm">
                    Wijzigen
                  </Button>
                </div>

                <div className="flex items-center justify-between">
                  <div>
                    <h3 className="font-medium text-neutral-900">Twee-factor authenticatie</h3>
                    <p className="text-sm text-neutral-600">Extra beveiliging voor je account</p>
                  </div>
                  <Button variant="outline" size="sm">
                    Instellen
                  </Button>
                </div>

                <div className="flex items-center justify-between">
                  <div>
                    <h3 className="font-medium text-neutral-900">Privacy instellingen</h3>
                    <p className="text-sm text-neutral-600">Bepaal hoe je gegevens gebruikt worden</p>
                  </div>
                  <Button variant="outline" size="sm" icon={Eye}>
                    Bekijk
                  </Button>
                </div>

                <div className="pt-4 border-t border-neutral-200">
                  <div className="flex items-center justify-between">
                    <div>
                      <h3 className="font-medium text-red-600">Account verwijderen</h3>
                      <p className="text-sm text-neutral-600">Permanent verwijder je account en alle gegevens</p>
                    </div>
                    <Button variant="ghost" size="sm" className="text-red-600 hover:bg-red-50">
                      Verwijderen
                    </Button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}