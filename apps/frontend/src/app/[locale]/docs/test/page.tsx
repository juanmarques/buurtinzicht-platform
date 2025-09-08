'use client';

import React, { useState } from 'react';
import Link from 'next/link';
import { ArrowLeft, Play, Copy, Check, RefreshCw, AlertCircle } from 'lucide-react';
import { Button } from '../../../components/ui/Button';
import { Input } from '../../../components/ui/Input';

export default function APITestPage() {
  const [selectedEndpoint, setSelectedEndpoint] = useState('search');
  const [apiKey, setApiKey] = useState('demo-api-key-12345');
  const [requestBody, setRequestBody] = useState('');
  const [response, setResponse] = useState<any>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [copiedText, setCopiedText] = useState<string | null>(null);

  const endpoints = {
    search: {
      title: 'Spatial Search',
      method: 'POST',
      url: '/api/spatial/search',
      defaultBody: JSON.stringify({
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
      }, null, 2)
    },
    neighborhoods: {
      title: 'Neighborhood Details',
      method: 'GET',
      url: '/api/neighborhoods/11001',
      defaultBody: ''
    },
    scorecard: {
      title: 'Generate Scorecard',
      method: 'POST',
      url: '/api/scorecard/generate',
      defaultBody: JSON.stringify({
        nisCode: '11001',
        preferences: {
          safety: 9,
          transport: 8,
          amenities: 7,
          culture: 6,
          nightlife: 5
        }
      }, null, 2)
    }
  };

  const currentEndpoint = endpoints[selectedEndpoint as keyof typeof endpoints];

  React.useEffect(() => {
    setRequestBody(currentEndpoint.defaultBody);
    setResponse(null);
    setError(null);
  }, [selectedEndpoint, currentEndpoint.defaultBody]);

  const copyToClipboard = (text: string, type: string) => {
    navigator.clipboard.writeText(text);
    setCopiedText(type);
    setTimeout(() => setCopiedText(null), 2000);
  };

  const executeRequest = async () => {
    if (!apiKey.trim()) {
      setError('API key is vereist');
      return;
    }

    setIsLoading(true);
    setError(null);
    
    try {
      // Simulate API call - in real implementation, this would call the actual API
      await new Promise(resolve => setTimeout(resolve, 1000 + Math.random() * 1000));
      
      // Mock response based on endpoint
      let mockResponse;
      switch (selectedEndpoint) {
        case 'search':
          mockResponse = {
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
                distanceKm: 0,
                area: 32.61
              },
              {
                nisCode: '11002',
                nameNl: 'Etterbeek',
                municipalityNl: 'Etterbeek',
                urbanizationLevel: 'URBAN',
                population: 47000,
                centroid: { latitude: 50.8229, longitude: 4.3889 },
                distanceKm: 3.2,
                area: 3.15
              }
            ],
            totalFound: 2,
            processingTimeMs: 125,
            metadata: {
              apiVersion: '1.0',
              timestamp: new Date().toISOString()
            }
          };
          break;
        
        case 'neighborhoods':
          mockResponse = {
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
              nightlife: 9.0,
              greenSpace: 6.2,
              costOfLiving: 7.8
            },
            demographics: {
              averageAge: 35.2,
              households: 85000,
              foreignerPercentage: 42.1
            }
          };
          break;
        
        case 'scorecard':
          mockResponse = {
            nisCode: '11001',
            overallScore: 8.4,
            categoryScores: {
              safety: { score: 7.5, weight: 9, weightedScore: 8.1 },
              transport: { score: 9.2, weight: 8, weightedScore: 8.8 },
              amenities: { score: 8.8, weight: 7, weightedScore: 8.4 },
              culture: { score: 9.5, weight: 6, weightedScore: 8.7 },
              nightlife: { score: 9.0, weight: 5, weightedScore: 7.9 }
            },
            insights: [
              'Uitstekende openbaar vervoer verbindingen met metro, tram en bus',
              'Levendige culturele scene met vele musea en theaters',
              'Hoge concentratie van restaurants en cafés',
              'Veiligheidsindex kan verbeterd worden in bepaalde gebieden'
            ],
            generatedAt: new Date().toISOString(),
            validUntil: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000).toISOString()
          };
          break;
        
        default:
          mockResponse = { message: 'OK' };
      }

      setResponse({
        status: 200,
        statusText: 'OK',
        headers: {
          'Content-Type': 'application/json',
          'X-RateLimit-Remaining': '999',
          'X-RateLimit-Reset': new Date(Date.now() + 3600000).toISOString()
        },
        data: mockResponse,
        responseTime: Math.floor(100 + Math.random() * 500)
      });

    } catch (err) {
      setError(err instanceof Error ? err.message : 'Er is een fout opgetreden');
    } finally {
      setIsLoading(false);
    }
  };

  const resetForm = () => {
    setRequestBody(currentEndpoint.defaultBody);
    setResponse(null);
    setError(null);
  };

  return (
    <div className="min-h-screen bg-neutral-50">
      {/* Header */}
      <div className="bg-white border-b border-neutral-200">
        <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8 py-6">
          <div className="flex items-center justify-between">
            <div className="flex items-center">
              <Link href="/docs">
                <Button variant="ghost" icon={ArrowLeft} className="mr-4">
                  Terug naar documentatie
                </Button>
              </Link>
              <div>
                <h1 className="text-3xl font-bold text-neutral-900">API Tester</h1>
                <p className="text-neutral-600 mt-1">Test de Buurtinzicht API endpoints live</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8 py-8">
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
          {/* Request Panel */}
          <div className="space-y-6">
            {/* API Key */}
            <div className="bg-white rounded-lg shadow-sm border border-neutral-200 p-6">
              <h2 className="text-lg font-semibold text-neutral-900 mb-4">Authentication</h2>
              <Input
                label="API Key"
                value={apiKey}
                onChange={(e) => setApiKey(e.target.value)}
                placeholder="Voer je API key in..."
                type="password"
              />
              <p className="text-xs text-neutral-600 mt-1">
                Voor demo doeleinden kun je de standaard demo key gebruiken
              </p>
            </div>

            {/* Endpoint Selection */}
            <div className="bg-white rounded-lg shadow-sm border border-neutral-200 p-6">
              <h2 className="text-lg font-semibold text-neutral-900 mb-4">Endpoint</h2>
              <div className="space-y-3">
                {Object.entries(endpoints).map(([key, endpoint]) => (
                  <label key={key} className="flex items-center cursor-pointer">
                    <input
                      type="radio"
                      name="endpoint"
                      value={key}
                      checked={selectedEndpoint === key}
                      onChange={(e) => setSelectedEndpoint(e.target.value)}
                      className="mr-3"
                    />
                    <div className="flex-1">
                      <div className="flex items-center justify-between">
                        <span className="font-medium">{endpoint.title}</span>
                        <span className={`text-xs px-2 py-1 rounded ${
                          endpoint.method === 'GET' ? 'bg-green-100 text-green-700' : 'bg-blue-100 text-blue-700'
                        }`}>
                          {endpoint.method}
                        </span>
                      </div>
                      <code className="text-sm text-neutral-600">{endpoint.url}</code>
                    </div>
                  </label>
                ))}
              </div>
            </div>

            {/* Request Body */}
            {currentEndpoint.method !== 'GET' && (
              <div className="bg-white rounded-lg shadow-sm border border-neutral-200 p-6">
                <div className="flex items-center justify-between mb-4">
                  <h2 className="text-lg font-semibold text-neutral-900">Request Body</h2>
                  <Button
                    variant="ghost"
                    size="sm"
                    icon={RefreshCw}
                    onClick={resetForm}
                  >
                    Reset
                  </Button>
                </div>
                <textarea
                  value={requestBody}
                  onChange={(e) => setRequestBody(e.target.value)}
                  className="w-full h-64 p-3 border border-neutral-300 rounded-lg font-mono text-sm focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
                  placeholder="JSON request body..."
                />
              </div>
            )}

            {/* Execute Button */}
            <div className="flex space-x-4">
              <Button
                variant="primary"
                onClick={executeRequest}
                disabled={isLoading}
                icon={isLoading ? RefreshCw : Play}
                className="flex-1"
              >
                {isLoading ? 'Uitvoeren...' : 'Voer verzoek uit'}
              </Button>
            </div>
          </div>

          {/* Response Panel */}
          <div className="space-y-6">
            {error && (
              <div className="bg-red-50 border border-red-200 rounded-lg p-4">
                <div className="flex items-center">
                  <AlertCircle className="h-5 w-5 text-red-500 mr-2" />
                  <span className="text-red-800 font-medium">Fout</span>
                </div>
                <p className="text-red-700 mt-1">{error}</p>
              </div>
            )}

            {response && (
              <>
                {/* Response Status */}
                <div className="bg-white rounded-lg shadow-sm border border-neutral-200 p-6">
                  <div className="flex items-center justify-between mb-4">
                    <h2 className="text-lg font-semibold text-neutral-900">Response</h2>
                    <div className="flex items-center space-x-4 text-sm">
                      <span className={`px-2 py-1 rounded ${
                        response.status === 200 ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'
                      }`}>
                        {response.status} {response.statusText}
                      </span>
                      <span className="text-neutral-600">{response.responseTime}ms</span>
                    </div>
                  </div>

                  {/* Response Headers */}
                  <div className="mb-4">
                    <h3 className="font-medium text-neutral-900 mb-2">Headers</h3>
                    <div className="bg-neutral-100 p-3 rounded text-sm font-mono">
                      {Object.entries(response.headers).map(([key, value]) => (
                        <div key={key}>
                          <span className="text-blue-600">{key}:</span>{' '}
                          <span className="text-neutral-700">{String(value)}</span>
                        </div>
                      ))}
                    </div>
                  </div>

                  {/* Response Body */}
                  <div>
                    <div className="flex items-center justify-between mb-2">
                      <h3 className="font-medium text-neutral-900">Body</h3>
                      <Button
                        variant="ghost"
                        size="sm"
                        icon={copiedText === 'response' ? Check : Copy}
                        onClick={() => copyToClipboard(JSON.stringify(response.data, null, 2), 'response')}
                      >
                        {copiedText === 'response' ? 'Gekopieerd!' : 'Kopieer'}
                      </Button>
                    </div>
                    <div className="bg-neutral-900 text-green-400 p-4 rounded-lg overflow-x-auto max-h-96">
                      <pre className="text-sm">
                        {JSON.stringify(response.data, null, 2)}
                      </pre>
                    </div>
                  </div>
                </div>
              </>
            )}

            {!response && !error && (
              <div className="bg-white rounded-lg shadow-sm border border-neutral-200 p-6">
                <div className="text-center py-8">
                  <Play className="h-12 w-12 text-neutral-400 mx-auto mb-4" />
                  <h3 className="text-lg font-medium text-neutral-900 mb-2">
                    Klaar om te testen
                  </h3>
                  <p className="text-neutral-600">
                    Selecteer een endpoint en voer het verzoek uit om de response te zien
                  </p>
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}