'use client';

import React, { useState } from 'react';
import Link from 'next/link';
import { MapPin, Users, BarChart3, Clock, ArrowRight, Star, Heart, Share2, Filter } from 'lucide-react';
import { Button } from '../ui/Button';
import type { SpatialQueryResponse } from '../../types/api';

interface SearchResultsProps {
  results: SpatialQueryResponse;
  onResultClick?: (nisCode: string) => void;
}

export function SearchResults({ results, onResultClick }: SearchResultsProps) {
  const [sortBy, setSortBy] = useState<'distance' | 'population' | 'name'>('distance');
  const [showFavorites, setShowFavorites] = useState(false);
  const [favorites, setFavorites] = useState<Set<string>>(new Set());
  const getUrbanizationIcon = (level: string) => {
    switch (level) {
      case 'RURAL': return '🌾';
      case 'SUBURBAN': return '🏡';
      case 'URBAN': return '🏙️';
      case 'METROPOLITAN': return '🌆';
      default: return '📍';
    }
  };

  const getUrbanizationLabel = (level: string) => {
    switch (level) {
      case 'RURAL': return 'Landelijk';
      case 'SUBURBAN': return 'Voorstedelijk';
      case 'URBAN': return 'Stedelijk';
      case 'METROPOLITAN': return 'Grootstedelijk';
      default: return level;
    }
  };

  const toggleFavorite = (nisCode: string) => {
    setFavorites(prev => {
      const newFavorites = new Set(prev);
      if (newFavorites.has(nisCode)) {
        newFavorites.delete(nisCode);
      } else {
        newFavorites.add(nisCode);
      }
      return newFavorites;
    });
  };

  const sortedResults = [...results.results].sort((a, b) => {
    switch (sortBy) {
      case 'distance':
        return a.distanceKm - b.distanceKm;
      case 'population':
        return (b.population || 0) - (a.population || 0);
      case 'name':
        return (a.nameNl || a.nameFr || a.nameEn || '').localeCompare(b.nameNl || b.nameFr || b.nameEn || '');
      default:
        return 0;
    }
  });

  const displayResults = showFavorites 
    ? sortedResults.filter(result => favorites.has(result.nisCode))
    : sortedResults;

  return (
    <div className="p-6">
      {/* Header */}
      <div className="mb-6">
        <div className="flex items-center justify-between mb-2">
          <h2 className="text-lg font-semibold text-neutral-900">
            Zoekresultaten
          </h2>
          <div className="flex items-center text-sm text-neutral-500">
            <Clock className="h-4 w-4 mr-1" />
            {results.processingTimeMs}ms
          </div>
        </div>
        <p className="text-sm text-neutral-600 mb-4">
          {results.totalFound} {results.totalFound === 1 ? 'buurt gevonden' : 'buurten gevonden'} binnen {results.query.radiusKm}km
        </p>
        
        {/* Controls */}
        <div className="flex items-center justify-between gap-4">
          <div className="flex items-center gap-2">
            <label className="text-sm font-medium text-neutral-700">Sorteer op:</label>
            <select
              value={sortBy}
              onChange={(e) => setSortBy(e.target.value as 'distance' | 'population' | 'name')}
              className="text-sm border border-neutral-300 rounded px-3 py-1 focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
            >
              <option value="distance">Afstand</option>
              <option value="population">Inwoners</option>
              <option value="name">Naam</option>
            </select>
          </div>
          
          <div className="flex items-center gap-2">
            <Button
              variant={showFavorites ? "primary" : "outline"}
              size="sm"
              icon={Heart}
              onClick={() => setShowFavorites(!showFavorites)}
            >
              {showFavorites ? 'Alle' : 'Favorieten'} ({favorites.size})
            </Button>
          </div>
        </div>
      </div>

      {/* Results List */}
      <div className="space-y-4">
        {displayResults.length === 0 && showFavorites ? (
          <div className="p-8 text-center">
            <Heart className="h-12 w-12 text-neutral-400 mx-auto mb-4" />
            <h3 className="text-lg font-medium text-neutral-900 mb-2">
              Geen favorieten gevonden
            </h3>
            <p className="text-neutral-600">
              Voeg buurten toe aan je favorieten door op het hart icoon te klikken.
            </p>
          </div>
        ) : (
          displayResults.map((result, index) => (
          <div
            key={result.nisCode}
            className="border border-neutral-200 rounded-lg p-4 hover:border-primary-300 hover:shadow-sm transition-all"
          >
            {/* Header */}
            <div className="flex items-start justify-between mb-3">
              <div className="flex-1">
                <div className="flex items-center gap-2 mb-1">
                  <h3 className="font-semibold text-neutral-900">
                    {result.nameNl || result.nameFr || result.nameEn}
                  </h3>
                  <button
                    onClick={() => toggleFavorite(result.nisCode)}
                    className={`p-1 rounded-full transition-colors ${
                      favorites.has(result.nisCode)
                        ? 'text-red-500 hover:text-red-600'
                        : 'text-neutral-400 hover:text-red-500'
                    }`}
                  >
                    <Heart 
                      className={`h-4 w-4 ${favorites.has(result.nisCode) ? 'fill-current' : ''}`}
                    />
                  </button>
                </div>
                <div className="flex items-center text-sm text-neutral-600 space-x-3">
                  <span className="flex items-center">
                    <MapPin className="h-3 w-3 mr-1" />
                    {result.municipalityNl || result.municipalityFr}
                  </span>
                  <span className="flex items-center">
                    {getUrbanizationIcon(result.urbanizationLevel)}
                    <span className="ml-1">{getUrbanizationLabel(result.urbanizationLevel)}</span>
                  </span>
                  {result.distanceKm > 0 && (
                    <span className="text-neutral-500">
                      {result.distanceKm.toFixed(1)}km
                    </span>
                  )}
                </div>
              </div>
              <div className="text-right">
                {result.population && (
                  <div className="flex items-center text-sm text-neutral-600">
                    <Users className="h-3 w-3 mr-1" />
                    {result.population.toLocaleString()}
                  </div>
                )}
              </div>
            </div>

            {/* Stats */}
            {(result.area || result.elevation) && (
              <div className="grid grid-cols-2 gap-4 mb-4 text-sm">
                {result.area && (
                  <div className="bg-neutral-50 p-2 rounded">
                    <div className="text-neutral-600 text-xs">Oppervlakte</div>
                    <div className="font-medium">{result.area.toFixed(1)} km²</div>
                  </div>
                )}
                {result.elevation && (
                  <div className="bg-neutral-50 p-2 rounded">
                    <div className="text-neutral-600 text-xs">Hoogte</div>
                    <div className="font-medium">{result.elevation.average}m gem.</div>
                  </div>
                )}
              </div>
            )}

            {/* Actions */}
            <div className="flex items-center justify-between pt-3 border-t border-neutral-100">
              <div className="flex space-x-2">
                <Button variant="outline" size="sm" onClick={() => onResultClick?.(result.nisCode)}>
                  Bekijk details
                </Button>
                <Button 
                  variant="ghost" 
                  size="sm" 
                  icon={Share2}
                  onClick={() => {
                    if (navigator.share) {
                      navigator.share({
                        title: `${result.nameNl || result.nameFr || result.nameEn}`,
                        text: `Ontdek deze buurt: ${result.nameNl || result.nameFr || result.nameEn} in ${result.municipalityNl || result.municipalityFr}`,
                        url: window.location.href
                      });
                    } else {
                      navigator.clipboard.writeText(window.location.href);
                    }
                  }}
                >
                  Delen
                </Button>
              </div>
              <div className="flex space-x-2">
                <Button variant="ghost" size="sm" icon={BarChart3}>
                  Scorecard
                </Button>
                <Link href={`/neighborhoods/${result.nisCode}`}>
                  <Button variant="primary" size="sm" iconPosition="right" icon={ArrowRight}>
                    Bekijk
                  </Button>
                </Link>
              </div>
            </div>
          </div>
        ))
        )}
      </div>

      {/* Load More */}
      {results.results.length < results.totalFound && (
        <div className="mt-6 text-center">
          <Button variant="outline" className="w-full">
            Laad meer resultaten ({results.totalFound - results.results.length} resterend)
          </Button>
        </div>
      )}
    </div>
  );
}