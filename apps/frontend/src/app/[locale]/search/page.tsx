'use client';

import React, { useState } from 'react';
import dynamic from 'next/dynamic';
import { Search, MapPin, Filter, Loader2 } from 'lucide-react';
import { Button } from '../../components/ui/Button';
import { SearchForm } from '../../components/forms/SearchForm';
import { SearchResults } from '../../components/search/SearchResults';
import type { SpatialQueryRequest, SpatialQueryResponse } from '../../types/api';

// Dynamically import Map component to avoid SSR issues with Leaflet
const Map = dynamic(() => import('../../components/maps/Map').then(mod => ({ default: mod.Map })), {
  ssr: false,
  loading: () => <div className="h-96 bg-neutral-100 rounded-lg animate-pulse flex items-center justify-center">
    <Loader2 className="h-8 w-8 animate-spin text-neutral-400" />
  </div>
});

export default function SearchPage() {
  const [searchQuery, setSearchQuery] = useState<SpatialQueryRequest | null>(null);
  const [searchResults, setSearchResults] = useState<SpatialQueryResponse | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [mapCenter, setMapCenter] = useState<[number, number]>([50.8503, 4.3517]); // Brussels
  const [showFilters, setShowFilters] = useState(false);

  const handleSearch = async (query: SpatialQueryRequest) => {
    setIsLoading(true);
    setSearchQuery(query);
    
    try {
      // TODO: Replace with actual API call
      // const results = await spatialApi.searchNeighborhoods(query);
      
      // Mock response for now
      const mockResults: SpatialQueryResponse = {
        query,
        results: [
          {
            nisCode: '11001',
            nameNl: 'Centrum - Brussel',
            nameFr: 'Centre - Bruxelles',
            nameEn: 'Centre - Brussels',
            province: 'Brussels-Capital Region',
            region: 'Brussels-Capital Region',
            municipalityNl: 'Brussel',
            municipalityFr: 'Bruxelles',
            urbanizationLevel: 'METROPOLITAN',
            population: 180000,
            centroid: { latitude: 50.8503, longitude: 4.3517 },
            distanceKm: 0,
            area: 32.61,
            elevation: { min: 13, max: 125, average: 57 }
          },
          {
            nisCode: '11002', 
            nameNl: 'Etterbeek',
            nameFr: 'Etterbeek',
            nameEn: 'Etterbeek',
            province: 'Brussels-Capital Region',
            region: 'Brussels-Capital Region',
            municipalityNl: 'Etterbeek',
            municipalityFr: 'Etterbeek',
            urbanizationLevel: 'URBAN',
            population: 47000,
            centroid: { latitude: 50.8229, longitude: 4.3889 },
            distanceKm: 3.2,
            area: 3.15,
            elevation: { min: 45, max: 85, average: 65 }
          }
        ],
        totalFound: 2,
        processingTimeMs: 125,
        metadata: {
          language: query.language || 'nl',
          searchRadius: query.radiusKm,
          centerPoint: { latitude: query.latitude, longitude: query.longitude },
          boundingBox: {
            north: query.latitude + 0.1,
            south: query.latitude - 0.1,
            east: query.longitude + 0.1, 
            west: query.longitude - 0.1
          }
        }
      };
      
      setSearchResults(mockResults);
      setMapCenter([query.latitude, query.longitude]);
    } catch (error) {
      console.error('Search failed:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const mapMarkers = searchResults?.results.map(result => ({
    position: [result.centroid.latitude, result.centroid.longitude] as [number, number],
    popup: `<strong>${result.nameNl || result.nameFr}</strong><br/>Population: ${result.population?.toLocaleString()}<br/>Distance: ${result.distanceKm}km`,
    title: result.nameNl || result.nameFr || result.nameEn
  })) || [];

  return (
    <div className="min-h-screen bg-neutral-50">
      {/* Header */}
      <div className="bg-white border-b border-neutral-200">
        <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8 py-8">
          <div className="text-center mb-8">
            <h1 className="text-3xl sm:text-4xl font-bold text-neutral-900 mb-4">
              Zoek je ideale buurt
            </h1>
            <p className="text-lg text-neutral-600 max-w-2xl mx-auto">
              Vind buurten die bij je levensstijl passen. Gebruik filters om te zoeken op voorzieningen, veiligheid, transport en meer.
            </p>
          </div>

          {/* Search Form */}
          <div className="max-w-4xl mx-auto">
            <SearchForm onSearch={handleSearch} isLoading={isLoading} />
            
            {/* Filter Toggle */}
            <div className="mt-4 flex justify-center">
              <Button
                variant="outline"
                size="sm"
                icon={Filter}
                onClick={() => setShowFilters(!showFilters)}
                className={showFilters ? 'bg-primary-50 border-primary-200 text-primary-700' : ''}
              >
                {showFilters ? 'Verberg filters' : 'Toon filters'}
              </Button>
            </div>
          </div>
        </div>
      </div>

      {/* Main Content */}
      <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8 py-8">
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
          {/* Map */}
          <div className="order-2 lg:order-1">
            <div className="bg-white rounded-lg shadow-sm border border-neutral-200 p-4">
              <h2 className="text-lg font-semibold text-neutral-900 mb-4 flex items-center">
                <MapPin className="h-5 w-5 mr-2 text-primary-600" />
                Kaart
              </h2>
              <Map
                center={mapCenter}
                zoom={searchResults ? 11 : 10}
                height="500px"
                markers={mapMarkers}
                onMapClick={(lat, lng) => {
                  console.log('Map clicked:', lat, lng);
                  // Could trigger a new search at this location
                }}
              />
            </div>
          </div>

          {/* Search Results */}
          <div className="order-1 lg:order-2">
            <div className="bg-white rounded-lg shadow-sm border border-neutral-200">
              {isLoading ? (
                <div className="p-8 text-center">
                  <Loader2 className="h-8 w-8 animate-spin text-primary-600 mx-auto mb-4" />
                  <p className="text-neutral-600">Zoeken naar buurten...</p>
                </div>
              ) : searchResults ? (
                <SearchResults results={searchResults} />
              ) : (
                <div className="p-8 text-center">
                  <Search className="h-12 w-12 text-neutral-400 mx-auto mb-4" />
                  <h3 className="text-lg font-medium text-neutral-900 mb-2">
                    Begin met zoeken
                  </h3>
                  <p className="text-neutral-600">
                    Voer een locatie in om buurten in de omgeving te vinden.
                  </p>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}