'use client';

import React, { useState } from 'react';
import Link from 'next/link';
import { 
  Plus, 
  X, 
  Search, 
  BarChart3, 
  Users, 
  MapPin,
  Shield,
  Bus,
  ShoppingBag,
  Star,
  TreePine,
  DollarSign,
  TrendingUp,
  ArrowRight
} from 'lucide-react';
import { Button } from '../../components/ui/Button';
import { Input } from '../../components/ui/Input';
import { useTranslations } from 'next-intl';

interface NeighborhoodComparison {
  nisCode: string;
  nameNl: string;
  municipalityNl: string;
  urbanizationLevel: string;
  population: number;
  area: number;
  metrics: {
    safety: number;
    transport: number;
    amenities: number;
    culture: number;
    nightlife: number;
    greenSpace: number;
    costOfLiving: number;
  };
}

export default function ComparePage() {
  const t = useTranslations('compare');
  const [selectedNeighborhoods, setSelectedNeighborhoods] = useState<NeighborhoodComparison[]>([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [searchResults, setSearchResults] = useState<NeighborhoodComparison[]>([]);
  const [isSearching, setIsSearching] = useState(false);

  // Mock data for available neighborhoods
  const availableNeighborhoods: NeighborhoodComparison[] = [
    {
      nisCode: '11001',
      nameNl: 'Centrum - Brussel',
      municipalityNl: 'Brussel',
      urbanizationLevel: 'METROPOLITAN',
      population: 180000,
      area: 32.61,
      metrics: {
        safety: 7.5,
        transport: 9.2,
        amenities: 8.8,
        culture: 9.5,
        nightlife: 9.0,
        greenSpace: 6.2,
        costOfLiving: 7.8
      }
    },
    {
      nisCode: '11002',
      nameNl: 'Etterbeek',
      municipalityNl: 'Etterbeek',
      urbanizationLevel: 'URBAN',
      population: 47000,
      area: 3.15,
      metrics: {
        safety: 8.2,
        transport: 8.8,
        amenities: 7.5,
        culture: 6.8,
        nightlife: 5.2,
        greenSpace: 7.8,
        costOfLiving: 6.5
      }
    },
    {
      nisCode: '11003',
      nameNl: 'Sint-Gillis',
      municipalityNl: 'Sint-Gillis',
      urbanizationLevel: 'URBAN',
      population: 50000,
      area: 2.5,
      metrics: {
        safety: 7.8,
        transport: 8.5,
        amenities: 8.2,
        culture: 8.9,
        nightlife: 8.5,
        greenSpace: 5.8,
        costOfLiving: 7.2
      }
    }
  ];

  const handleSearch = async (term: string) => {
    if (!term.trim()) {
      setSearchResults([]);
      return;
    }

    setIsSearching(true);
    
    // Simulate API search
    await new Promise(resolve => setTimeout(resolve, 500));
    
    const results = availableNeighborhoods.filter(neighborhood =>
      neighborhood.nameNl.toLowerCase().includes(term.toLowerCase()) ||
      neighborhood.municipalityNl.toLowerCase().includes(term.toLowerCase())
    );
    
    setSearchResults(results);
    setIsSearching(false);
  };

  const addNeighborhood = (neighborhood: NeighborhoodComparison) => {
    if (selectedNeighborhoods.length >= 4) {
      alert(t('maxNeighborhoods'));
      return;
    }
    
    if (!selectedNeighborhoods.find(n => n.nisCode === neighborhood.nisCode)) {
      setSelectedNeighborhoods([...selectedNeighborhoods, neighborhood]);
      setSearchTerm('');
      setSearchResults([]);
    }
  };

  const removeNeighborhood = (nisCode: string) => {
    setSelectedNeighborhoods(selectedNeighborhoods.filter(n => n.nisCode !== nisCode));
  };

  const getUrbanizationLabel = (level: string) => {
    switch (level) {
      case 'RURAL': return t('urbanization.rural');
      case 'SUBURBAN': return t('urbanization.suburban');
      case 'URBAN': return t('urbanization.urban');
      case 'METROPOLITAN': return t('urbanization.metropolitan');
      default: return level;
    }
  };

  const getMetricIcon = (metric: string) => {
    switch (metric) {
      case 'safety': return Shield;
      case 'transport': return Bus;
      case 'amenities': return ShoppingBag;
      case 'culture': return Star;
      case 'nightlife': return Star;
      case 'greenSpace': return TreePine;
      case 'costOfLiving': return DollarSign;
      default: return BarChart3;
    }
  };

  const getMetricLabel = (metric: string) => {
    return t(`metrics.${metric}`);
  };

  const getScoreColor = (score: number) => {
    if (score >= 8) return 'bg-green-500';
    if (score >= 6) return 'bg-yellow-500';
    return 'bg-red-500';
  };

  const getBestScore = (metric: string) => {
    return Math.max(...selectedNeighborhoods.map(n => n.metrics[metric as keyof typeof n.metrics]));
  };

  return (
    <div className="min-h-screen bg-neutral-50">
      {/* Header */}
      <div className="bg-white border-b border-neutral-200">
        <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8 py-8">
          <div className="text-center mb-8">
            <h1 className="text-3xl sm:text-4xl font-bold text-neutral-900 mb-4 flex items-center justify-center">
              <BarChart3 className="h-8 w-8 mr-3 text-primary-600" />
              {t('title')}
            </h1>
            <p className="text-lg text-neutral-600 max-w-3xl mx-auto">
              {t('subtitle')}
            </p>
          </div>

          {/* Search */}
          <div className="max-w-2xl mx-auto">
            <div className="relative">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-5 w-5 text-neutral-400" />
              <input
                type="text"
                placeholder={t('searchPlaceholder')}
                value={searchTerm}
                onChange={(e) => {
                  setSearchTerm(e.target.value);
                  handleSearch(e.target.value);
                }}
                className="w-full pl-10 pr-3 py-3 text-lg border border-neutral-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
              />
            </div>
            
            {/* Search Results */}
            {searchResults.length > 0 && (
              <div className="mt-4 bg-white border border-neutral-200 rounded-lg shadow-lg max-h-64 overflow-y-auto">
                {searchResults.map((neighborhood) => (
                  <button
                    key={neighborhood.nisCode}
                    onClick={() => addNeighborhood(neighborhood)}
                    className="w-full px-4 py-3 text-left hover:bg-neutral-50 border-b border-neutral-100 last:border-b-0 flex items-center justify-between"
                  >
                    <div>
                      <div className="font-medium text-neutral-900">{neighborhood.nameNl}</div>
                      <div className="text-sm text-neutral-600">{neighborhood.municipalityNl}</div>
                    </div>
                    <Plus className="h-4 w-4 text-primary-600" />
                  </button>
                ))}
              </div>
            )}
          </div>
        </div>
      </div>

      <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8 py-8">
        {selectedNeighborhoods.length === 0 ? (
          <div className="text-center py-16">
            <BarChart3 className="h-16 w-16 text-neutral-400 mx-auto mb-4" />
            <h2 className="text-2xl font-bold text-neutral-900 mb-2">
              {t('empty.title')}
            </h2>
            <p className="text-neutral-600 mb-6">
              {t('empty.subtitle')}
            </p>
            <Link href="/search">
              <Button variant="primary" size="lg">
                {t('empty.cta')}
              </Button>
            </Link>
          </div>
        ) : (
          <div className="space-y-8">
            {/* Selected Neighborhoods Overview */}
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
              {selectedNeighborhoods.map((neighborhood) => (
                <div key={neighborhood.nisCode} className="bg-white rounded-lg shadow-sm border border-neutral-200 p-4">
                  <div className="flex items-start justify-between mb-3">
                    <div className="flex-1">
                      <h3 className="font-semibold text-neutral-900 text-sm">{neighborhood.nameNl}</h3>
                      <p className="text-xs text-neutral-600">{neighborhood.municipalityNl}</p>
                    </div>
                    <button
                      onClick={() => removeNeighborhood(neighborhood.nisCode)}
                      className="text-neutral-400 hover:text-red-500 transition-colors"
                    >
                      <X className="h-4 w-4" />
                    </button>
                  </div>
                  <div className="space-y-2 text-xs">
                    <div className="flex justify-between">
                      <span className="text-neutral-600">{t('overview.population')}:</span>
                      <span className="font-medium">{neighborhood.population.toLocaleString()}</span>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-neutral-600">{t('overview.type')}:</span>
                      <span className="font-medium">{getUrbanizationLabel(neighborhood.urbanizationLevel)}</span>
                    </div>
                  </div>
                </div>
              ))}
              
              {/* Add More Button */}
              {selectedNeighborhoods.length < 4 && (
                <div className="bg-white rounded-lg shadow-sm border-2 border-dashed border-neutral-300 p-4 flex items-center justify-center">
                  <div className="text-center">
                    <Plus className="h-8 w-8 text-neutral-400 mx-auto mb-2" />
                    <p className="text-sm text-neutral-600">{t('addMore')}</p>
                  </div>
                </div>
              )}
            </div>

            {/* Comparison Table */}
            <div className="bg-white rounded-lg shadow-sm border border-neutral-200 overflow-hidden">
              <div className="p-6 border-b border-neutral-200">
                <h2 className="text-xl font-semibold text-neutral-900">{t('table.title')}</h2>
                <p className="text-sm text-neutral-600 mt-1">{t('table.subtitle')}</p>
              </div>
              
              <div className="overflow-x-auto">
                <table className="w-full">
                  <thead className="bg-neutral-50">
                    <tr>
                      <th className="px-6 py-3 text-left text-xs font-medium text-neutral-500 uppercase tracking-wider">
                        {t('table.category')}
                      </th>
                      {selectedNeighborhoods.map((neighborhood) => (
                        <th key={neighborhood.nisCode} className="px-6 py-3 text-left text-xs font-medium text-neutral-500 uppercase tracking-wider">
                          {neighborhood.nameNl}
                        </th>
                      ))}
                    </tr>
                  </thead>
                  <tbody className="bg-white divide-y divide-neutral-200">
                    {Object.keys(selectedNeighborhoods[0]?.metrics || {}).map((metric) => {
                      const Icon = getMetricIcon(metric);
                      const bestScore = getBestScore(metric);
                      
                      return (
                        <tr key={metric}>
                          <td className="px-6 py-4 whitespace-nowrap">
                            <div className="flex items-center">
                              <Icon className="h-4 w-4 text-neutral-600 mr-2" />
                              <span className="text-sm font-medium text-neutral-900">
                                {getMetricLabel(metric)}
                              </span>
                            </div>
                          </td>
                          {selectedNeighborhoods.map((neighborhood) => {
                            const score = neighborhood.metrics[metric as keyof typeof neighborhood.metrics];
                            const isHighest = score === bestScore && selectedNeighborhoods.length > 1;
                            
                            return (
                              <td key={neighborhood.nisCode} className="px-6 py-4 whitespace-nowrap">
                                <div className="flex items-center">
                                  <div className={`w-2 h-2 rounded-full mr-2 ${getScoreColor(score)}`} />
                                  <span className={`text-sm font-medium ${isHighest ? 'text-green-600' : 'text-neutral-900'}`}>
                                    {score.toFixed(1)}
                                    {isHighest && ' 🏆'}
                                  </span>
                                </div>
                                <div className="w-full bg-neutral-200 rounded-full h-1 mt-1">
                                  <div 
                                    className={`h-1 rounded-full ${getScoreColor(score)}`}
                                    style={{ width: `${(score / 10) * 100}%` }}
                                  />
                                </div>
                              </td>
                            );
                          })}
                        </tr>
                      );
                    })}
                  </tbody>
                </table>
              </div>
            </div>

            {/* Actions */}
            <div className="bg-white rounded-lg shadow-sm border border-neutral-200 p-6">
              <h3 className="text-lg font-semibold text-neutral-900 mb-4">{t('actions.title')}</h3>
              <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
                <Button variant="outline" className="flex items-center justify-center" icon={MapPin}>
                  {t('actions.viewOnMap')}
                </Button>
                <Button variant="outline" className="flex items-center justify-center" icon={BarChart3}>
                  {t('actions.export')}
                </Button>
                <Link href="/search">
                  <Button variant="primary" className="w-full flex items-center justify-center" iconPosition="right" icon={ArrowRight}>
                    {t('actions.searchMore')}
                  </Button>
                </Link>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}