'use client';

import React, { useState } from 'react';
import Link from 'next/link';
import { 
  BarChart3, 
  Download, 
  Eye, 
  Calendar, 
  MapPin, 
  TrendingUp,
  Filter,
  Search,
  Plus,
  Star
} from 'lucide-react';
import { Button } from '../../components/ui/Button';
import { Input } from '../../components/ui/Input';

interface Scorecard {
  id: string;
  neighborhoodName: string;
  neighborhoodCode: string;
  municipality: string;
  overallScore: number;
  generatedAt: string;
  preferences: {
    safety: number;
    transport: number;
    amenities: number;
    culture: number;
    nightlife: number;
  };
  status: 'active' | 'expired' | 'draft';
}

export default function ScorecardsPage() {
  const [searchTerm, setSearchTerm] = useState('');
  const [filterStatus, setFilterStatus] = useState<'all' | 'active' | 'expired' | 'draft'>('all');
  const [sortBy, setSortBy] = useState<'date' | 'score' | 'name'>('date');

  const [scorecards] = useState<Scorecard[]>([
    {
      id: '1',
      neighborhoodName: 'Centrum - Brussel',
      neighborhoodCode: '11001',
      municipality: 'Brussel',
      overallScore: 8.4,
      generatedAt: '2024-01-20T10:30:00Z',
      preferences: {
        safety: 9,
        transport: 8,
        amenities: 7,
        culture: 6,
        nightlife: 5
      },
      status: 'active'
    },
    {
      id: '2',
      neighborhoodName: 'Etterbeek',
      neighborhoodCode: '11002',
      municipality: 'Etterbeek',
      overallScore: 7.8,
      generatedAt: '2024-01-19T14:15:00Z',
      preferences: {
        safety: 8,
        transport: 7,
        amenities: 6,
        culture: 5,
        nightlife: 4
      },
      status: 'active'
    },
    {
      id: '3',
      neighborhoodName: 'Sint-Gillis',
      neighborhoodCode: '11003',
      municipality: 'Sint-Gillis',
      overallScore: 8.1,
      generatedAt: '2024-01-15T09:00:00Z',
      preferences: {
        safety: 7,
        transport: 8,
        amenities: 8,
        culture: 9,
        nightlife: 8
      },
      status: 'expired'
    }
  ]);

  const filteredScorecards = scorecards
    .filter(scorecard => {
      const matchesSearch = searchTerm === '' || 
        scorecard.neighborhoodName.toLowerCase().includes(searchTerm.toLowerCase()) ||
        scorecard.municipality.toLowerCase().includes(searchTerm.toLowerCase());
      
      const matchesStatus = filterStatus === 'all' || scorecard.status === filterStatus;
      
      return matchesSearch && matchesStatus;
    })
    .sort((a, b) => {
      switch (sortBy) {
        case 'date':
          return new Date(b.generatedAt).getTime() - new Date(a.generatedAt).getTime();
        case 'score':
          return b.overallScore - a.overallScore;
        case 'name':
          return a.neighborhoodName.localeCompare(b.neighborhoodName);
        default:
          return 0;
      }
    });

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'active': return 'bg-green-100 text-green-800';
      case 'expired': return 'bg-red-100 text-red-800';
      case 'draft': return 'bg-yellow-100 text-yellow-800';
      default: return 'bg-gray-100 text-gray-800';
    }
  };

  const getStatusLabel = (status: string) => {
    switch (status) {
      case 'active': return 'Actief';
      case 'expired': return 'Verlopen';
      case 'draft': return 'Concept';
      default: return status;
    }
  };

  const getScoreColor = (score: number) => {
    if (score >= 8) return 'text-green-600';
    if (score >= 6) return 'text-yellow-600';
    return 'text-red-600';
  };

  return (
    <div className="min-h-screen bg-neutral-50">
      {/* Header */}
      <div className="bg-white border-b border-neutral-200">
        <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8 py-8">
          <div className="text-center mb-8">
            <h1 className="text-3xl sm:text-4xl font-bold text-neutral-900 mb-4 flex items-center justify-center">
              <BarChart3 className="h-8 w-8 mr-3 text-primary-600" />
              Mijn Scorecards
            </h1>
            <p className="text-lg text-neutral-600 max-w-3xl mx-auto">
              Bekijk, beheer en deel je gepersonaliseerde buurt scorecards
            </p>
          </div>

          {/* Actions */}
          <div className="flex flex-col sm:flex-row justify-between items-center gap-4">
            <div className="flex flex-col sm:flex-row gap-4 w-full sm:w-auto">
              {/* Search */}
              <div className="relative">
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-neutral-400" />
                <input
                  type="text"
                  placeholder="Zoek scorecards..."
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  className="pl-10 pr-3 py-2 border border-neutral-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
                />
              </div>

              {/* Filters */}
              <select
                value={filterStatus}
                onChange={(e) => setFilterStatus(e.target.value as typeof filterStatus)}
                className="px-3 py-2 border border-neutral-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
              >
                <option value="all">Alle statussen</option>
                <option value="active">Actief</option>
                <option value="expired">Verlopen</option>
                <option value="draft">Concept</option>
              </select>

              <select
                value={sortBy}
                onChange={(e) => setSortBy(e.target.value as typeof sortBy)}
                className="px-3 py-2 border border-neutral-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
              >
                <option value="date">Datum</option>
                <option value="score">Score</option>
                <option value="name">Naam</option>
              </select>
            </div>

            <Link href="/search">
              <Button variant="primary" icon={Plus}>
                Nieuwe Scorecard
              </Button>
            </Link>
          </div>
        </div>
      </div>

      <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8 py-8">
        {filteredScorecards.length === 0 ? (
          <div className="text-center py-16">
            {searchTerm || filterStatus !== 'all' ? (
              <>
                <Search className="h-16 w-16 text-neutral-400 mx-auto mb-4" />
                <h2 className="text-2xl font-bold text-neutral-900 mb-2">
                  Geen scorecards gevonden
                </h2>
                <p className="text-neutral-600 mb-6">
                  Probeer je zoekterm aan te passen of wijzig je filters
                </p>
                <div className="flex justify-center gap-4">
                  <Button variant="outline" onClick={() => setSearchTerm('')}>
                    Wis zoekterm
                  </Button>
                  <Button variant="outline" onClick={() => setFilterStatus('all')}>
                    Wis filters
                  </Button>
                </div>
              </>
            ) : (
              <>
                <BarChart3 className="h-16 w-16 text-neutral-400 mx-auto mb-4" />
                <h2 className="text-2xl font-bold text-neutral-900 mb-2">
                  Nog geen scorecards
                </h2>
                <p className="text-neutral-600 mb-6">
                  Begin met het zoeken naar buurten om je eerste scorecard aan te maken
                </p>
                <Link href="/search">
                  <Button variant="primary" size="lg">
                    Ontdek buurten
                  </Button>
                </Link>
              </>
            )}
          </div>
        ) : (
          <div className="grid grid-cols-1 lg:grid-cols-2 xl:grid-cols-3 gap-6">
            {filteredScorecards.map((scorecard) => (
              <div key={scorecard.id} className="bg-white rounded-lg shadow-sm border border-neutral-200 hover:shadow-md transition-shadow">
                {/* Header */}
                <div className="p-6 border-b border-neutral-100">
                  <div className="flex items-start justify-between mb-3">
                    <div className="flex-1">
                      <h3 className="font-semibold text-neutral-900 mb-1">
                        {scorecard.neighborhoodName}
                      </h3>
                      <p className="text-sm text-neutral-600 flex items-center">
                        <MapPin className="h-3 w-3 mr-1" />
                        {scorecard.municipality}
                      </p>
                    </div>
                    <span className={`px-2 py-1 rounded-full text-xs font-medium ${getStatusColor(scorecard.status)}`}>
                      {getStatusLabel(scorecard.status)}
                    </span>
                  </div>
                  
                  <div className="flex items-center justify-between">
                    <div className="flex items-center">
                      <TrendingUp className="h-4 w-4 text-neutral-400 mr-2" />
                      <span className={`text-2xl font-bold ${getScoreColor(scorecard.overallScore)}`}>
                        {scorecard.overallScore.toFixed(1)}
                      </span>
                      <span className="text-sm text-neutral-500 ml-1">/10</span>
                    </div>
                    <div className="flex items-center text-xs text-neutral-500">
                      <Calendar className="h-3 w-3 mr-1" />
                      {new Date(scorecard.generatedAt).toLocaleDateString('nl-BE')}
                    </div>
                  </div>
                </div>

                {/* Preferences Preview */}
                <div className="p-6 border-b border-neutral-100">
                  <h4 className="text-sm font-medium text-neutral-900 mb-3">Jouw voorkeuren</h4>
                  <div className="space-y-2">
                    {Object.entries(scorecard.preferences).slice(0, 3).map(([key, value]) => (
                      <div key={key} className="flex items-center justify-between text-sm">
                        <span className="text-neutral-600 capitalize">
                          {key === 'safety' ? 'Veiligheid' : 
                           key === 'transport' ? 'Transport' : 
                           key === 'amenities' ? 'Voorzieningen' : 
                           key === 'culture' ? 'Cultuur' : 'Nachtleven'}
                        </span>
                        <div className="flex items-center">
                          {[...Array(5)].map((_, i) => (
                            <Star 
                              key={i} 
                              className={`h-3 w-3 ${i < value ? 'text-yellow-400 fill-current' : 'text-neutral-300'}`} 
                            />
                          ))}
                        </div>
                      </div>
                    ))}
                    {Object.keys(scorecard.preferences).length > 3 && (
                      <div className="text-xs text-neutral-500">
                        +{Object.keys(scorecard.preferences).length - 3} meer...
                      </div>
                    )}
                  </div>
                </div>

                {/* Actions */}
                <div className="p-6">
                  <div className="flex space-x-2">
                    <Link href={`/scorecards/${scorecard.id}`} className="flex-1">
                      <Button variant="primary" size="sm" icon={Eye} className="w-full">
                        Bekijk
                      </Button>
                    </Link>
                    <Button variant="outline" size="sm" icon={Download}>
                      Export
                    </Button>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}