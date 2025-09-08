'use client';

import React, { useState } from 'react';
import Link from 'next/link';
import { Heart, MapPin, Users, BarChart3, ArrowRight, Star, Search, Filter, Trash2, Share2 } from 'lucide-react';
import { Button } from '../../components/ui/Button';
import { useTranslations } from 'next-intl';

export default function FavoritesPage() {
  const t = useTranslations('favorites');
  const [favorites, setFavorites] = useState([
    {
      nisCode: '11001',
      nameNl: 'Centrum - Brussel',
      nameFr: 'Centre - Bruxelles',
      nameEn: 'Centre - Brussels',
      municipalityNl: 'Brussel',
      municipalityFr: 'Bruxelles',
      urbanizationLevel: 'METROPOLITAN',
      population: 180000,
      area: 32.61,
      savedDate: '2024-01-20',
      rating: 4.5,
      notes: 'Levendige atmosfeer, veel cultuur'
    },
    {
      nisCode: '11002',
      nameNl: 'Etterbeek',
      nameFr: 'Etterbeek',
      nameEn: 'Etterbeek',
      municipalityNl: 'Etterbeek',
      municipalityFr: 'Etterbeek',
      urbanizationLevel: 'URBAN',
      population: 47000,
      area: 3.15,
      savedDate: '2024-01-19',
      rating: 4.2,
      notes: 'Rustige buurt met goede verbindingen'
    },
    {
      nisCode: '11003',
      nameNl: 'Saint-Gilles',
      nameFr: 'Sint-Gillis',
      nameEn: 'Saint-Gilles',
      municipalityNl: 'Sint-Gillis',
      municipalityFr: 'Saint-Gilles',
      urbanizationLevel: 'URBAN',
      population: 50000,
      area: 2.5,
      savedDate: '2024-01-18',
      rating: 4.7,
      notes: 'Artistieke buurt met veel cafés'
    }
  ]);

  const [sortBy, setSortBy] = useState<'date' | 'rating' | 'name'>('date');
  const [filterBy, setFilterBy] = useState<'all' | 'urban' | 'suburban' | 'rural' | 'metropolitan'>('all');
  const [searchTerm, setSearchTerm] = useState('');

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
    return t(`urbanization.${level.toLowerCase()}`);
  };

  const removeFavorite = (nisCode: string) => {
    setFavorites(prev => prev.filter(fav => fav.nisCode !== nisCode));
  };

  const shareNeighborhood = (neighborhood: typeof favorites[0]) => {
    if (navigator.share) {
      navigator.share({
        title: `${neighborhood.nameNl || neighborhood.nameFr}`,
        text: t('share.text', { name: neighborhood.nameNl || neighborhood.nameFr, municipality: neighborhood.municipalityNl || neighborhood.municipalityFr }),
        url: `${window.location.origin}/neighborhoods/${neighborhood.nisCode}`
      });
    } else {
      navigator.clipboard.writeText(`${window.location.origin}/neighborhoods/${neighborhood.nisCode}`);
    }
  };

  const filteredAndSortedFavorites = favorites
    .filter(fav => {
      if (searchTerm) {
        const searchLower = searchTerm.toLowerCase();
        return (
          (fav.nameNl && fav.nameNl.toLowerCase().includes(searchLower)) ||
          (fav.nameFr && fav.nameFr.toLowerCase().includes(searchLower)) ||
          (fav.municipalityNl && fav.municipalityNl.toLowerCase().includes(searchLower)) ||
          (fav.municipalityFr && fav.municipalityFr.toLowerCase().includes(searchLower))
        );
      }
      return true;
    })
    .filter(fav => {
      if (filterBy === 'all') return true;
      return fav.urbanizationLevel.toLowerCase() === filterBy.toLowerCase();
    })
    .sort((a, b) => {
      switch (sortBy) {
        case 'date':
          return new Date(b.savedDate).getTime() - new Date(a.savedDate).getTime();
        case 'rating':
          return b.rating - a.rating;
        case 'name':
          return (a.nameNl || a.nameFr || '').localeCompare(b.nameNl || b.nameFr || '');
        default:
          return 0;
      }
    });

  return (
    <div className="min-h-screen bg-neutral-50">
      {/* Header */}
      <div className="bg-white border-b border-neutral-200">
        <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8 py-8">
          <div className="text-center mb-8">
            <h1 className="text-3xl sm:text-4xl font-bold text-neutral-900 mb-4 flex items-center justify-center">
              <Heart className="h-8 w-8 mr-3 text-red-500" />
              {t('title')}
            </h1>
            <p className="text-lg text-neutral-600 max-w-2xl mx-auto">
              {t('subtitle', { count: favorites.length })}
            </p>
          </div>

          {/* Search and Filter Controls */}
          <div className="max-w-4xl mx-auto space-y-4">
            <div className="flex flex-col sm:flex-row gap-4">
              <div className="flex-1">
                <div className="relative">
                  <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-neutral-400" />
                  <input
                    type="text"
                    placeholder={t('searchPlaceholder')}
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    className="w-full pl-10 pr-3 py-2 border border-neutral-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
                  />
                </div>
              </div>
              <div className="flex gap-2">
                <select
                  value={sortBy}
                  onChange={(e) => setSortBy(e.target.value as 'date' | 'rating' | 'name')}
                  className="px-3 py-2 border border-neutral-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
                >
                  <option value="date">{t('sort.newest')}</option>
                  <option value="rating">{t('sort.rating')}</option>
                  <option value="name">{t('sort.name')}</option>
                </select>
                <select
                  value={filterBy}
                  onChange={(e) => setFilterBy(e.target.value as typeof filterBy)}
                  className="px-3 py-2 border border-neutral-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
                >
                  <option value="all">{t('filter.all')}</option>
                  <option value="metropolitan">{t('filter.metropolitan')}</option>
                  <option value="urban">{t('filter.urban')}</option>
                  <option value="suburban">{t('filter.suburban')}</option>
                  <option value="rural">{t('filter.rural')}</option>
                </select>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Main Content */}
      <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8 py-8">
        {filteredAndSortedFavorites.length === 0 ? (
          <div className="text-center py-16">
            {searchTerm || filterBy !== 'all' ? (
              <>
                <Search className="h-16 w-16 text-neutral-400 mx-auto mb-4" />
                <h2 className="text-2xl font-bold text-neutral-900 mb-2">
                  {t('noResults.title')}
                </h2>
                <p className="text-neutral-600 mb-6">
                  {t('noResults.subtitle')}
                </p>
                <div className="flex justify-center gap-4">
                  <Button variant="outline" onClick={() => setSearchTerm('')}>
                    {t('noResults.clearSearch')}
                  </Button>
                  <Button variant="outline" onClick={() => setFilterBy('all')}>
                    {t('noResults.clearFilters')}
                  </Button>
                </div>
              </>
            ) : (
              <>
                <Heart className="h-16 w-16 text-neutral-400 mx-auto mb-4" />
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
              </>
            )}
          </div>
        ) : (
          <div className="grid grid-cols-1 lg:grid-cols-2 xl:grid-cols-3 gap-6">
            {filteredAndSortedFavorites.map((favorite) => (
              <div key={favorite.nisCode} className="bg-white rounded-lg shadow-sm border border-neutral-200 hover:shadow-md transition-shadow">
                {/* Header */}
                <div className="p-6 border-b border-neutral-100">
                  <div className="flex items-start justify-between">
                    <div className="flex-1">
                      <h3 className="font-semibold text-neutral-900 mb-1">
                        {favorite.nameNl || favorite.nameFr || favorite.nameEn}
                      </h3>
                      <div className="flex items-center text-sm text-neutral-600 space-x-3 mb-2">
                        <span className="flex items-center">
                          <MapPin className="h-3 w-3 mr-1" />
                          {favorite.municipalityNl || favorite.municipalityFr}
                        </span>
                        <span className="flex items-center">
                          {getUrbanizationIcon(favorite.urbanizationLevel)}
                          <span className="ml-1">{getUrbanizationLabel(favorite.urbanizationLevel)}</span>
                        </span>
                      </div>
                      <div className="flex items-center">
                        <div className="flex items-center mr-2">
                          {[...Array(5)].map((_, i) => (
                            <Star 
                              key={i} 
                              className={`h-4 w-4 ${i < Math.floor(favorite.rating) ? 'text-yellow-500 fill-current' : 'text-neutral-300'}`} 
                            />
                          ))}
                        </div>
                        <span className="text-sm text-neutral-600">{favorite.rating}/5</span>
                      </div>
                    </div>
                    <button
                      onClick={() => removeFavorite(favorite.nisCode)}
                      className="p-1 text-neutral-400 hover:text-red-500 transition-colors"
                      title={t('remove')}
                    >
                      <Trash2 className="h-4 w-4" />
                    </button>
                  </div>
                </div>

                {/* Stats */}
                <div className="p-6 border-b border-neutral-100">
                  <div className="grid grid-cols-2 gap-4 text-sm">
                    <div className="bg-neutral-50 p-3 rounded">
                      <div className="text-neutral-600 text-xs mb-1">{t('stats.population')}</div>
                      <div className="font-medium flex items-center">
                        <Users className="h-3 w-3 mr-1" />
                        {favorite.population?.toLocaleString()}
                      </div>
                    </div>
                    <div className="bg-neutral-50 p-3 rounded">
                      <div className="text-neutral-600 text-xs mb-1">{t('stats.area')}</div>
                      <div className="font-medium">{favorite.area?.toFixed(1)} km²</div>
                    </div>
                  </div>
                </div>

                {/* Notes */}
                {favorite.notes && (
                  <div className="px-6 py-3 border-b border-neutral-100">
                    <div className="text-xs text-neutral-600 mb-1">{t('notes')}:</div>
                    <p className="text-sm text-neutral-800 italic">\"{favorite.notes}\"</p>
                  </div>
                )}

                {/* Actions */}
                <div className="p-6">
                  <div className="flex items-center justify-between">
                    <div className="text-xs text-neutral-500">
                      {t('addedOn', { date: new Date(favorite.savedDate).toLocaleDateString('nl-BE') })}
                    </div>
                    <div className="flex space-x-2">
                      <Button 
                        variant="ghost" 
                        size="sm" 
                        icon={Share2}
                        onClick={() => shareNeighborhood(favorite)}
                      >
                        {t('actions.share')}
                      </Button>
                      <Button variant="ghost" size="sm" icon={BarChart3}>
                        {t('actions.scorecard')}
                      </Button>
                      <Link href={`/neighborhoods/${favorite.nisCode}`}>
                        <Button variant="primary" size="sm" iconPosition="right" icon={ArrowRight}>
                          {t('actions.view')}
                        </Button>
                      </Link>
                    </div>
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