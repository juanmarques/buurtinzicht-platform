'use client';

import React, { useState } from 'react';
import Link from 'next/link';
import { ArrowLeft, Settings, Globe, Palette, Database, Download, Trash2 } from 'lucide-react';
import { Button } from '../../../components/ui/Button';

export default function SettingsPage() {
  const [settings, setSettings] = useState({
    theme: 'light',
    language: 'nl',
    defaultRadius: 5,
    defaultFilters: {
      showRural: true,
      showSuburban: true,
      showUrban: true,
      showMetropolitan: true,
    },
    dataRetention: 30,
    autoSave: true,
    analytics: false,
  });

  const [isSaving, setIsSaving] = useState(false);

  const handleSave = async () => {
    setIsSaving(true);
    // Simulate API call
    await new Promise(resolve => setTimeout(resolve, 1000));
    setIsSaving(false);
  };

  const handleSettingChange = (key: string, value: any) => {
    setSettings(prev => ({ ...prev, [key]: value }));
  };

  const handleFilterChange = (filter: string, value: boolean) => {
    setSettings(prev => ({
      ...prev,
      defaultFilters: {
        ...prev.defaultFilters,
        [filter]: value
      }
    }));
  };

  const exportData = () => {
    // Simulate data export
    alert('Data export wordt voorbereid. Je ontvangt een e-mail wanneer het klaar is.');
  };

  const clearData = () => {
    if (confirm('Weet je zeker dat je alle opgeslagen zoekopdrachten wilt verwijderen?')) {
      alert('Alle zoekopdrachten zijn verwijderd.');
    }
  };

  return (
    <div className="min-h-screen bg-neutral-50">
      {/* Header */}
      <div className="bg-white border-b border-neutral-200">
        <div className="mx-auto max-w-4xl px-4 sm:px-6 lg:px-8 py-6">
          <div className="flex items-center justify-between">
            <div className="flex items-center">
              <Link href="/dashboard">
                <Button variant="ghost" icon={ArrowLeft} className="mr-4">
                  Terug naar dashboard
                </Button>
              </Link>
              <div>
                <h1 className="text-3xl font-bold text-neutral-900">Instellingen</h1>
                <p className="text-neutral-600 mt-1">Pas de applicatie aan naar jouw voorkeuren</p>
              </div>
            </div>
            <Button 
              variant="primary" 
              onClick={handleSave} 
              disabled={isSaving}
            >
              {isSaving ? 'Opslaan...' : 'Wijzigingen opslaan'}
            </Button>
          </div>
        </div>
      </div>

      <div className="mx-auto max-w-4xl px-4 sm:px-6 lg:px-8 py-8">
        <div className="space-y-8">
          {/* Appearance */}
          <div className="bg-white rounded-lg shadow-sm border border-neutral-200">
            <div className="p-6 border-b border-neutral-200">
              <h2 className="text-lg font-semibold text-neutral-900 flex items-center">
                <Palette className="h-5 w-5 mr-2 text-primary-600" />
                Uiterlijk
              </h2>
            </div>
            <div className="p-6 space-y-6">
              <div>
                <label className="block text-sm font-medium text-neutral-700 mb-3">
                  Thema
                </label>
                <div className="grid grid-cols-3 gap-3">
                  {['light', 'dark', 'auto'].map((theme) => (
                    <label key={theme} className="cursor-pointer">
                      <input
                        type="radio"
                        name="theme"
                        value={theme}
                        checked={settings.theme === theme}
                        onChange={(e) => handleSettingChange('theme', e.target.value)}
                        className="sr-only"
                      />
                      <div className={`
                        p-4 border-2 rounded-lg text-center transition-all
                        ${settings.theme === theme 
                          ? 'border-primary-600 bg-primary-50 text-primary-900' 
                          : 'border-neutral-200 hover:border-neutral-300'
                        }
                      `}>
                        <div className="font-medium">
                          {theme === 'light' ? 'Licht' : theme === 'dark' ? 'Donker' : 'Automatisch'}
                        </div>
                      </div>
                    </label>
                  ))}
                </div>
              </div>
            </div>
          </div>

          {/* Language & Region */}
          <div className="bg-white rounded-lg shadow-sm border border-neutral-200">
            <div className="p-6 border-b border-neutral-200">
              <h2 className="text-lg font-semibold text-neutral-900 flex items-center">
                <Globe className="h-5 w-5 mr-2 text-primary-600" />
                Taal & Regio
              </h2>
            </div>
            <div className="p-6 space-y-6">
              <div>
                <label className="block text-sm font-medium text-neutral-700 mb-1">
                  Taal
                </label>
                <select
                  value={settings.language}
                  onChange={(e) => handleSettingChange('language', e.target.value)}
                  className="block w-full max-w-xs rounded-lg border border-neutral-300 shadow-sm px-3 py-2 focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
                >
                  <option value="nl">Nederlands</option>
                  <option value="fr">Français</option>
                  <option value="en">English</option>
                </select>
              </div>
            </div>
          </div>

          {/* Search Defaults */}
          <div className="bg-white rounded-lg shadow-sm border border-neutral-200">
            <div className="p-6 border-b border-neutral-200">
              <h2 className="text-lg font-semibold text-neutral-900 flex items-center">
                <Settings className="h-5 w-5 mr-2 text-primary-600" />
                Standaard zoekinstellingen
              </h2>
            </div>
            <div className="p-6 space-y-6">
              <div>
                <label className="block text-sm font-medium text-neutral-700 mb-1">
                  Standaard zoekradius (km)
                </label>
                <input
                  type="number"
                  min="1"
                  max="50"
                  value={settings.defaultRadius}
                  onChange={(e) => handleSettingChange('defaultRadius', parseInt(e.target.value))}
                  className="block w-full max-w-xs rounded-lg border border-neutral-300 shadow-sm px-3 py-2 focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-neutral-700 mb-3">
                  Standaard urbanisatieniveaus
                </label>
                <div className="space-y-2">
                  {[
                    { key: 'showRural', label: 'Landelijk' },
                    { key: 'showSuburban', label: 'Voorstedelijk' },
                    { key: 'showUrban', label: 'Stedelijk' },
                    { key: 'showMetropolitan', label: 'Grootstedelijk' },
                  ].map(({ key, label }) => (
                    <label key={key} className="flex items-center">
                      <input
                        type="checkbox"
                        checked={settings.defaultFilters[key as keyof typeof settings.defaultFilters]}
                        onChange={(e) => handleFilterChange(key, e.target.checked)}
                        className="rounded border-neutral-300 text-primary-600 focus:ring-primary-500 mr-2"
                      />
                      {label}
                    </label>
                  ))}
                </div>
              </div>

              <div className="flex items-center">
                <input
                  type="checkbox"
                  id="autoSave"
                  checked={settings.autoSave}
                  onChange={(e) => handleSettingChange('autoSave', e.target.checked)}
                  className="rounded border-neutral-300 text-primary-600 focus:ring-primary-500 mr-2"
                />
                <label htmlFor="autoSave" className="text-sm font-medium text-neutral-700">
                  Automatisch zoekopdrachten opslaan
                </label>
              </div>
            </div>
          </div>

          {/* Data Management */}
          <div className="bg-white rounded-lg shadow-sm border border-neutral-200">
            <div className="p-6 border-b border-neutral-200">
              <h2 className="text-lg font-semibold text-neutral-900 flex items-center">
                <Database className="h-5 w-5 mr-2 text-primary-600" />
                Gegevensbeheer
              </h2>
            </div>
            <div className="p-6 space-y-6">
              <div>
                <label className="block text-sm font-medium text-neutral-700 mb-1">
                  Data bewaren (dagen)
                </label>
                <select
                  value={settings.dataRetention}
                  onChange={(e) => handleSettingChange('dataRetention', parseInt(e.target.value))}
                  className="block w-full max-w-xs rounded-lg border border-neutral-300 shadow-sm px-3 py-2 focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
                >
                  <option value={7}>7 dagen</option>
                  <option value={30}>30 dagen</option>
                  <option value={90}>90 dagen</option>
                  <option value={365}>1 jaar</option>
                  <option value={-1}>Permanent</option>
                </select>
                <p className="text-xs text-neutral-600 mt-1">
                  Hoe lang zoekopdrachten en voorkeuren bewaard blijven
                </p>
              </div>

              <div className="flex items-center">
                <input
                  type="checkbox"
                  id="analytics"
                  checked={settings.analytics}
                  onChange={(e) => handleSettingChange('analytics', e.target.checked)}
                  className="rounded border-neutral-300 text-primary-600 focus:ring-primary-500 mr-2"
                />
                <label htmlFor="analytics" className="text-sm font-medium text-neutral-700">
                  Anonieme gebruiksstatistieken delen om de app te verbeteren
                </label>
              </div>

              <div className="pt-4 border-t border-neutral-200">
                <div className="flex flex-col sm:flex-row gap-4">
                  <Button variant="outline" icon={Download} onClick={exportData}>
                    Exporteer mijn gegevens
                  </Button>
                  <Button variant="ghost" icon={Trash2} onClick={clearData} className="text-red-600 hover:bg-red-50">
                    Wis alle zoekopdrachten
                  </Button>
                </div>
                <p className="text-xs text-neutral-600 mt-2">
                  Exporteer je gegevens naar een JSON-bestand of wis alle opgeslagen zoekopdrachten
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}