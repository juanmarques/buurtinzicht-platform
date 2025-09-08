'use client';

import React, { useState } from 'react';
import { Book, Play, Copy, Check, Code, FileText, Zap, Shield } from 'lucide-react';
import { Button } from '../../components/ui/Button';
import { useTranslations } from 'next-intl';

export default function DocsPage() {
  const t = useTranslations('docs');
  const [copiedEndpoint, setCopiedEndpoint] = useState<string | null>(null);
  const [selectedEndpoint, setSelectedEndpoint] = useState('search');

  const copyToClipboard = (text: string, endpoint: string) => {
    navigator.clipboard.writeText(text);
    setCopiedEndpoint(endpoint);
    setTimeout(() => setCopiedEndpoint(null), 2000);
  };

  const apiEndpoints = {
    search: {
      title: t('endpoints.search.title'),
      method: 'POST',
      endpoint: '/api/spatial/search',
      description: t('endpoints.search.description'),
      requestBody: {
        latitude: 50.8503,
        longitude: 4.3517,
        radiusKm: 5,
        language: 'nl',
        includeNeighboring: true,
        limit: 20,
        filters: {
          urbanizationLevels: ['URBAN', 'METROPOLITAN'],
          minPopulation: 10000,
          maxPopulation: 100000
        }
      },
      responseExample: {
        query: {
          latitude: 50.8503,
          longitude: 4.3517,
          radiusKm: 5
        },
        results: [
          {
            nisCode: '11001',
            nameNl: 'Centrum - Brussel',
            municipalityNl: 'Brussel',
            urbanizationLevel: 'METROPOLITAN',
            population: 180000,
            centroid: { latitude: 50.8503, longitude: 4.3517 },
            distanceKm: 0
          }
        ],
        totalFound: 1,
        processingTimeMs: 125
      }
    },
    neighborhoods: {
      title: t('endpoints.neighborhoods.title'),
      method: 'GET',
      endpoint: '/api/neighborhoods/{nisCode}',
      description: t('endpoints.neighborhoods.description'),
      requestBody: null,
      responseExample: {
        nisCode: '11001',
        nameNl: 'Centrum - Brussel',
        nameFr: 'Centre - Bruxelles',
        nameEn: 'Centre - Brussels',
        municipalityNl: 'Brussel',
        population: 180000,
        area: 32.61,
        urbanizationLevel: 'METROPOLITAN',
        centroid: { latitude: 50.8503, longitude: 4.3517 },
        metrics: {
          safety: 7.5,
          transport: 9.2,
          amenities: 8.8,
          culture: 9.5,
          nightlife: 9.0
        }
      }
    },
    scorecard: {
      title: t('endpoints.scorecard.title'),
      method: 'POST',
      endpoint: '/api/scorecard/generate',
      description: t('endpoints.scorecard.description'),
      requestBody: {
        nisCode: '11001',
        preferences: {
          safety: 9,
          transport: 8,
          amenities: 7,
          culture: 6,
          nightlife: 5
        }
      },
      responseExample: {
        nisCode: '11001',
        overallScore: 8.4,
        categoryScores: {
          safety: { score: 7.5, weight: 9, weightedScore: 8.1 },
          transport: { score: 9.2, weight: 8, weightedScore: 8.8 }
        },
        insights: [
          'Uitstekende openbaar vervoer verbindingen',
          'Levendige culturele scene'
        ],
        generatedAt: '2024-01-20T12:00:00Z'
      }
    }
  };

  const currentEndpoint = apiEndpoints[selectedEndpoint as keyof typeof apiEndpoints];

  return (
    <div className="min-h-screen bg-neutral-50">
      {/* Header */}
      <div className="bg-white border-b border-neutral-200">
        <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8 py-8">
          <div className="text-center mb-8">
            <h1 className="text-3xl sm:text-4xl font-bold text-neutral-900 mb-4 flex items-center justify-center">
              <Book className="h-8 w-8 mr-3 text-primary-600" />
              {t('title')}
            </h1>
            <p className="text-lg text-neutral-600 max-w-3xl mx-auto">
              {t('subtitle')}
            </p>
          </div>

          {/* Quick Stats */}
          <div className="grid grid-cols-1 md:grid-cols-4 gap-6 max-w-4xl mx-auto">
            <div className="bg-primary-50 border border-primary-200 rounded-lg p-4 text-center">
              <Zap className="h-6 w-6 text-primary-600 mx-auto mb-2" />
              <div className="font-semibold text-primary-900">{t('quickStats.apiType.label')}</div>
              <div className="text-sm text-primary-700">{t('quickStats.apiType.value')}</div>
            </div>
            <div className="bg-green-50 border border-green-200 rounded-lg p-4 text-center">
              <Shield className="h-6 w-6 text-green-600 mx-auto mb-2" />
              <div className="font-semibold text-green-900">{t('quickStats.authentication.label')}</div>
              <div className="text-sm text-green-700">{t('quickStats.authentication.value')}</div>
            </div>
            <div className="bg-blue-50 border border-blue-200 rounded-lg p-4 text-center">
              <Code className="h-6 w-6 text-blue-600 mx-auto mb-2" />
              <div className="font-semibold text-blue-900">{t('quickStats.rateLimit.label')}</div>
              <div className="text-sm text-blue-700">{t('quickStats.rateLimit.value')}</div>
            </div>
            <div className="bg-purple-50 border border-purple-200 rounded-lg p-4 text-center">
              <FileText className="h-6 w-6 text-purple-600 mx-auto mb-2" />
              <div className="font-semibold text-purple-900">{t('quickStats.sdks.label')}</div>
              <div className="text-sm text-purple-700">{t('quickStats.sdks.value')}</div>
            </div>
          </div>
        </div>
      </div>

      <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8 py-8">
        <div className="grid grid-cols-1 lg:grid-cols-4 gap-8">
          {/* Sidebar Navigation */}
          <div className="lg:col-span-1">
            <div className="bg-white rounded-lg shadow-sm border border-neutral-200 p-4 sticky top-8">
              <h3 className="font-semibold text-neutral-900 mb-4">{t('sidebar.endpoints')}</h3>
              <nav className="space-y-2">
                {Object.entries(apiEndpoints).map(([key, endpoint]) => (
                  <button
                    key={key}
                    onClick={() => setSelectedEndpoint(key)}
                    className={`w-full text-left px-3 py-2 rounded-lg text-sm font-medium transition-colors ${
                      selectedEndpoint === key
                        ? 'bg-primary-50 text-primary-700 border border-primary-200'
                        : 'text-neutral-700 hover:bg-neutral-50'
                    }`}
                  >
                    <div className="flex items-center justify-between">
                      <span>{endpoint.title}</span>
                      <span className={`text-xs px-2 py-1 rounded ${
                        endpoint.method === 'GET' ? 'bg-green-100 text-green-700' : 'bg-blue-100 text-blue-700'
                      }`}>
                        {endpoint.method}
                      </span>
                    </div>
                  </button>
                ))}
              </nav>

              <div className="mt-6 pt-4 border-t border-neutral-200">
                <h4 className="font-semibold text-neutral-900 mb-2">{t('sidebar.quickLinks')}</h4>
                <div className="space-y-1 text-sm">
                  <a href="#authentication" className="block text-primary-600 hover:text-primary-700">
                    {t('sidebar.authentication')}
                  </a>
                  <a href="#rate-limiting" className="block text-primary-600 hover:text-primary-700">
                    {t('sidebar.rateLimiting')}
                  </a>
                  <a href="#errors" className="block text-primary-600 hover:text-primary-700">
                    {t('sidebar.errorHandling')}
                  </a>
                  <a href="#sdks" className="block text-primary-600 hover:text-primary-700">
                    {t('sidebar.sdks')}
                  </a>
                </div>
              </div>
            </div>
          </div>

          {/* Main Content */}
          <div className="lg:col-span-3">
            <div className="bg-white rounded-lg shadow-sm border border-neutral-200">
              {/* Endpoint Header */}
              <div className="p-6 border-b border-neutral-200">
                <div className="flex items-center justify-between mb-2">
                  <h2 className="text-xl font-semibold text-neutral-900">
                    {currentEndpoint.title}
                  </h2>
                  <span className={`px-3 py-1 rounded text-sm font-medium ${
                    currentEndpoint.method === 'GET' 
                      ? 'bg-green-100 text-green-700' 
                      : 'bg-blue-100 text-blue-700'
                  }`}>
                    {currentEndpoint.method}
                  </span>
                </div>
                <div className="flex items-center justify-between">
                  <code className="text-lg font-mono text-neutral-700 bg-neutral-100 px-3 py-1 rounded">
                    {currentEndpoint.endpoint}
                  </code>
                  <Button
                    variant="ghost"
                    size="sm"
                    icon={copiedEndpoint === selectedEndpoint ? Check : Copy}
                    onClick={() => copyToClipboard(currentEndpoint.endpoint, selectedEndpoint)}
                  >
                    {copiedEndpoint === selectedEndpoint ? t('copy.copied') : t('copy.copy')}
                  </Button>
                </div>
                <p className="text-neutral-600 mt-2">
                  {currentEndpoint.description}
                </p>
              </div>

              {/* Request Body */}
              {currentEndpoint.requestBody && (
                <div className="p-6 border-b border-neutral-200">
                  <div className="flex items-center justify-between mb-3">
                    <h3 className="font-semibold text-neutral-900">{t('requestBody')}</h3>
                    <Button
                      variant="ghost"
                      size="sm"
                      icon={copiedEndpoint === `${selectedEndpoint}-request` ? Check : Copy}
                      onClick={() => copyToClipboard(
                        JSON.stringify(currentEndpoint.requestBody, null, 2),
                        `${selectedEndpoint}-request`
                      )}
                    >
                      {copiedEndpoint === `${selectedEndpoint}-request` ? t('copy.copied') : t('copy.copy')}
                    </Button>
                  </div>
                  <div className="bg-neutral-900 text-green-400 p-4 rounded-lg overflow-x-auto">
                    <pre className="text-sm">
                      {JSON.stringify(currentEndpoint.requestBody, null, 2)}
                    </pre>
                  </div>
                </div>
              )}

              {/* Response Example */}
              <div className="p-6">
                <div className="flex items-center justify-between mb-3">
                  <h3 className="font-semibold text-neutral-900">{t('responseExample')}</h3>
                  <div className="flex space-x-2">
                    <Button
                      variant="ghost"
                      size="sm"
                      icon={copiedEndpoint === `${selectedEndpoint}-response` ? Check : Copy}
                      onClick={() => copyToClipboard(
                        JSON.stringify(currentEndpoint.responseExample, null, 2),
                        `${selectedEndpoint}-response`
                      )}
                    >
                      {copiedEndpoint === `${selectedEndpoint}-response` ? t('copy.copied') : t('copy.copy')}
                    </Button>
                    <Button variant="outline" size="sm" icon={Play}>
                      {t('testApi')}
                    </Button>
                  </div>
                </div>
                <div className="bg-neutral-900 text-green-400 p-4 rounded-lg overflow-x-auto">
                  <pre className="text-sm">
                    {JSON.stringify(currentEndpoint.responseExample, null, 2)}
                  </pre>
                </div>
              </div>
            </div>

            {/* Additional Documentation Sections */}
            <div className="mt-8 space-y-8">
              {/* Authentication */}
              <div id="authentication" className="bg-white rounded-lg shadow-sm border border-neutral-200 p-6">
                <h3 className="text-lg font-semibold text-neutral-900 mb-3">{t('authentication.title')}</h3>
                <p className="text-neutral-600 mb-4">
                  {t('authentication.description')}
                </p>
                <div className="bg-neutral-900 text-green-400 p-4 rounded-lg">
                  <pre className="text-sm">
{`Authorization: Bearer YOUR_API_KEY
Content-Type: application/json`}
                  </pre>
                </div>
              </div>

              {/* Rate Limiting */}
              <div id="rate-limiting" className="bg-white rounded-lg shadow-sm border border-neutral-200 p-6">
                <h3 className="text-lg font-semibold text-neutral-900 mb-3">{t('rateLimiting.title')}</h3>
                <p className="text-neutral-600 mb-4">
                  {t('rateLimiting.description')}
                </p>
                <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-4">
                  <p className="text-yellow-800 text-sm">
                    <strong>{t('rateLimiting.note.title')}:</strong> {t('rateLimiting.note.content')}
                  </p>
                </div>
              </div>

              {/* Error Handling */}
              <div id="errors" className="bg-white rounded-lg shadow-sm border border-neutral-200 p-6">
                <h3 className="text-lg font-semibold text-neutral-900 mb-3">{t('errorHandling.title')}</h3>
                <p className="text-neutral-600 mb-4">
                  {t('errorHandling.description')}
                </p>
                <div className="bg-neutral-900 text-green-400 p-4 rounded-lg">
                  <pre className="text-sm">
{`{
  "error": {
    "code": "INVALID_COORDINATES",
    "message": "De opgegeven coördinaten zijn ongeldig",
    "details": {
      "latitude": "Moet tussen -90 en 90 liggen",
      "longitude": "Moet tussen -180 en 180 liggen"
    }
  }
}`}
                  </pre>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}