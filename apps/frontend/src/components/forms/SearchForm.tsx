'use client';

import React, { useState } from 'react';
import { Search, MapPin, Loader2 } from 'lucide-react';
import { Button } from '../ui/Button';
import { Input } from '../ui/Input';
import type { SpatialQueryRequest } from '../../types/api';

interface SearchFormProps {
  onSearch: (query: SpatialQueryRequest) => void;
  isLoading?: boolean;
}

export function SearchForm({ onSearch, isLoading = false }: SearchFormProps) {
  const [address, setAddress] = useState('');
  const [radius, setRadius] = useState(5);
  const [showAdvanced, setShowAdvanced] = useState(false);
  const [filters, setFilters] = useState({
    urbanizationLevels: [] as string[],
    minPopulation: '',
    maxPopulation: '',
  });

  const [isGeocoding, setIsGeocoding] = useState(false);

  const geocodeAddress = async (address: string): Promise<{ latitude: number; longitude: number } | null> => {
    try {
      // Use Nominatim API for geocoding (free alternative to Google)
      const response = await fetch(
        `https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(address + ', Belgium')}&limit=1&countrycodes=be`
      );
      const data = await response.json();
      
      if (data && data.length > 0) {
        return {
          latitude: parseFloat(data[0].lat),
          longitude: parseFloat(data[0].lon)
        };
      }
      return null;
    } catch (error) {
      console.error('Geocoding failed:', error);
      return null;
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!address.trim()) return;

    setIsGeocoding(true);
    
    try {
      // Try to geocode the address
      const coordinates = await geocodeAddress(address);
      
      // Fallback to Brussels coordinates if geocoding fails
      const finalCoordinates = coordinates || { latitude: 50.8503, longitude: 4.3517 };

      const query: SpatialQueryRequest = {
        latitude: finalCoordinates.latitude,
        longitude: finalCoordinates.longitude,
        radiusKm: radius,
        language: 'nl',
        includeNeighboring: true,
        limit: 20,
        filters: {
          ...(filters.urbanizationLevels.length > 0 && {
            urbanizationLevels: filters.urbanizationLevels as ('RURAL' | 'SUBURBAN' | 'URBAN' | 'METROPOLITAN')[]
          }),
          ...(filters.minPopulation && { minPopulation: parseInt(filters.minPopulation) }),
          ...(filters.maxPopulation && { maxPopulation: parseInt(filters.maxPopulation) }),
        }
      };

      onSearch(query);
    } catch (error) {
      console.error('Search failed:', error);
    } finally {
      setIsGeocoding(false);
    }
  };

  const handleUrbanizationToggle = (level: string) => {
    setFilters(prev => ({
      ...prev,
      urbanizationLevels: prev.urbanizationLevels.includes(level)
        ? prev.urbanizationLevels.filter(l => l !== level)
        : [...prev.urbanizationLevels, level]
    }));
  };

  const urbanizationOptions = [
    { value: 'RURAL', label: 'Landelijk', description: 'Rustige, natuurrijke omgeving' },
    { value: 'SUBURBAN', label: 'Voorstedelijk', description: 'Woonwijken met voorzieningen' },
    { value: 'URBAN', label: 'Stedelijk', description: 'Levendige stadsomgeving' },
    { value: 'METROPOLITAN', label: 'Grootstedelijk', description: 'Centrum van grote stad' },
  ];

  return (
    <form onSubmit={handleSubmit} className="space-y-6">
      {/* Main Search */}
      <div className="relative">
        <div className="flex space-x-4">
          <div className="flex-1">
            <Input
              type="text"
              placeholder="Voer adres, postcode of plaatsnaam in..."
              value={address}
              onChange={(e) => setAddress(e.target.value)}
              icon={MapPin}
              className="text-lg py-3"
              required
            />
          </div>
          <div className="w-32">
            <Input
              type="number"
              placeholder="Radius (km)"
              value={radius}
              onChange={(e) => setRadius(parseInt(e.target.value) || 5)}
              min="1"
              max="50"
              className="py-3"
            />
          </div>
          <Button 
            type="submit" 
            size="lg"
            disabled={isLoading || isGeocoding || !address.trim()}
            icon={(isLoading || isGeocoding) ? Loader2 : Search}
            className="px-8"
          >
            {isGeocoding ? 'Locatie zoeken...' : (isLoading ? 'Zoeken...' : 'Zoeken')}
          </Button>
        </div>
      </div>

      {/* Advanced Filters Toggle */}
      <div className="text-center">
        <button
          type="button"
          onClick={() => setShowAdvanced(!showAdvanced)}
          className="text-primary-600 hover:text-primary-700 text-sm font-medium underline"
        >
          {showAdvanced ? 'Verberg geavanceerde opties' : 'Toon geavanceerde opties'}
        </button>
      </div>

      {/* Advanced Filters */}
      {showAdvanced && (
        <div className="border-t border-neutral-200 pt-6 space-y-6">
          {/* Urbanization Level */}
          <div>
            <label className="block text-sm font-medium text-neutral-700 mb-3">
              Type omgeving
            </label>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
              {urbanizationOptions.map((option) => (
                <label
                  key={option.value}
                  className={`
                    flex items-start p-3 border rounded-lg cursor-pointer transition-all
                    ${filters.urbanizationLevels.includes(option.value)
                      ? 'border-primary-300 bg-primary-50 text-primary-900'
                      : 'border-neutral-200 hover:border-neutral-300 text-neutral-700'
                    }
                  `}
                >
                  <input
                    type="checkbox"
                    className="mt-1 rounded border-neutral-300 text-primary-600 focus:ring-primary-500"
                    checked={filters.urbanizationLevels.includes(option.value)}
                    onChange={() => handleUrbanizationToggle(option.value)}
                  />
                  <div className="ml-3">
                    <div className="font-medium">{option.label}</div>
                    <div className="text-sm text-neutral-500">{option.description}</div>
                  </div>
                </label>
              ))}
            </div>
          </div>

          {/* Population Range */}
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <Input
              type="number"
              label="Min. inwoners"
              placeholder="Bijv. 10000"
              value={filters.minPopulation}
              onChange={(e) => setFilters(prev => ({ ...prev, minPopulation: e.target.value }))}
              min="0"
            />
            <Input
              type="number"
              label="Max. inwoners"
              placeholder="Bijv. 100000"
              value={filters.maxPopulation}
              onChange={(e) => setFilters(prev => ({ ...prev, maxPopulation: e.target.value }))}
              min="0"
            />
          </div>
        </div>
      )}
    </form>
  );
}