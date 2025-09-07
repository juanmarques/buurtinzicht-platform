'use client';

import React, { useState, useEffect } from 'react';
import { useParams } from 'next/navigation';
import Link from 'next/link';
import { 
  MapPin, 
  Users, 
  BarChart3, 
  Heart, 
  Share2, 
  ArrowLeft,
  Star,
  Calendar,
  Shield,
  Bus,
  ShoppingBag,
  TreePine,
  DollarSign,
  TrendingUp,
  Info
} from 'lucide-react';
import { Button } from '../../../components/ui/Button';
import dynamic from 'next/dynamic';

// Dynamically import Map component
const Map = dynamic(() => import('../../../components/maps/Map').then(mod => ({ default: mod.Map })), {
  ssr: false,
  loading: () => <div className="h-64 bg-neutral-100 rounded-lg animate-pulse" />
});

interface NeighborhoodData {
  nisCode: string;
  nameNl: string;
  nameFr?: string;
  nameEn?: string;
  municipalityNl: string;
  municipalityFr?: string;
  province: string;
  region: string;
  urbanizationLevel: string;
  population: number;
  area: number;
  centroid: { latitude: number; longitude: number };
  metrics: {
    safety: number;
    transport: number;
    amenities: number;
    culture: number;
    nightlife: number;
    greenSpace: number;
    costOfLiving: number;
  };
  demographics: {
    averageAge: number;
    households: number;
    foreignerPercentage: number;
  };
  description?: string;
}

export default function NeighborhoodDetailPage() {
  const params = useParams();
  const nisCode = params.nisCode as string;
  const [neighborhood, setNeighborhood] = useState<NeighborhoodData | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isFavorite, setIsFavorite] = useState(false);

  useEffect(() => {
    const fetchNeighborhood = async () => {
      setIsLoading(true);
      
      // Simulate API call with mock data
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      const mockData: NeighborhoodData = {
        nisCode,
        nameNl: nisCode === '11001' ? 'Centrum - Brussel' : 'Etterbeek',
        nameFr: nisCode === '11001' ? 'Centre - Bruxelles' : 'Etterbeek',
        nameEn: nisCode === '11001' ? 'Centre - Brussels' : 'Etterbeek',
        municipalityNl: nisCode === '11001' ? 'Brussel' : 'Etterbeek',
        municipalityFr: nisCode === '11001' ? 'Bruxelles' : 'Etterbeek',
        province: 'Brussels-Capital Region',
        region: 'Brussels-Capital Region',
        urbanizationLevel: nisCode === '11001' ? 'METROPOLITAN' : 'URBAN',
        population: nisCode === '11001' ? 180000 : 47000,
        area: nisCode === '11001' ? 32.61 : 3.15,
        centroid: nisCode === '11001' 
          ? { latitude: 50.8503, longitude: 4.3517 }
          : { latitude: 50.8229, longitude: 4.3889 },
        metrics: {
          safety: nisCode === '11001' ? 7.5 : 8.2,
          transport: nisCode === '11001' ? 9.2 : 8.8,
          amenities: nisCode === '11001' ? 8.8 : 7.5,
          culture: nisCode === '11001' ? 9.5 : 6.8,
          nightlife: nisCode === '11001' ? 9.0 : 5.2,
          greenSpace: nisCode === '11001' ? 6.2 : 7.8,
          costOfLiving: nisCode === '11001' ? 7.8 : 6.5
        },
        demographics: {
          averageAge: nisCode === '11001' ? 35.2 : 38.5,
          households: nisCode === '11001' ? 85000 : 22000,
          foreignerPercentage: nisCode === '11001' ? 42.1 : 35.8
        },
        description: nisCode === '11001' 
          ? 'Het historische hart van Brussel met een rijke cultuur, uitstekende restaurants en levendig nachtleven.'
          : 'Een rustige woonwijk met goede verbindingen en veel groenvoorzieningen.'
      };

      setNeighborhood(mockData);
      setIsLoading(false);
    };

    if (nisCode) {
      fetchNeighborhood();
    }
  }, [nisCode]);

  const toggleFavorite = () => {
    setIsFavorite(!isFavorite);
  };

  const shareNeighborhood = () => {
    if (navigator.share && neighborhood) {
      navigator.share({
        title: neighborhood.nameNl,
        text: `Ontdek ${neighborhood.nameNl} in ${neighborhood.municipalityNl}`,
        url: window.location.href
      });
    } else {
      navigator.clipboard.writeText(window.location.href);
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

  const getMetricIcon = (metric: string) => {
    switch (metric) {
      case 'safety': return Shield;
      case 'transport': return Bus;
      case 'amenities': return ShoppingBag;
      case 'culture': return Star;
      case 'greenSpace': return TreePine;
      case 'costOfLiving': return DollarSign;
      default: return BarChart3;
    }
  };

  const getMetricLabel = (metric: string) => {
    switch (metric) {
      case 'safety': return 'Veiligheid';
      case 'transport': return 'Openbaar Vervoer';
      case 'amenities': return 'Voorzieningen';
      case 'culture': return 'Cultuur';
      case 'nightlife': return 'Nachtleven';
      case 'greenSpace': return 'Groenvoorziening';
      case 'costOfLiving': return 'Kosten van Leven';
      default: return metric;
    }
  };

  const getScoreColor = (score: number) => {
    if (score >= 8) return 'text-green-600 bg-green-50';
    if (score >= 6) return 'text-yellow-600 bg-yellow-50';
    return 'text-red-600 bg-red-50';
  };

  if (isLoading) {
    return (
      <div className="min-h-screen bg-neutral-50 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600 mx-auto mb-4"></div>
          <p className="text-neutral-600">Buurtinformatie laden...</p>
        </div>
      </div>
    );
  }

  if (!neighborhood) {
    return (
      <div className="min-h-screen bg-neutral-50 flex items-center justify-center">
        <div className="text-center">
          <h1 className="text-2xl font-bold text-neutral-900 mb-2">Buurt niet gevonden</h1>
          <p className="text-neutral-600 mb-4">De opgevraagde buurt kon niet worden geladen.</p>
          <Link href="/search">
            <Button variant="primary">Terug naar zoeken</Button>
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-neutral-50">
      {/* Header */}
      <div className="bg-white border-b border-neutral-200">
        <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8 py-6">
          <div className="flex items-center justify-between">
            <div className="flex items-center">
              <Link href="/search">
                <Button variant="ghost" icon={ArrowLeft} className="mr-4">
                  Terug naar zoeken
                </Button>
              </Link>
              <div>
                <h1 className="text-3xl font-bold text-neutral-900">{neighborhood.nameNl}</h1>
                <p className="text-neutral-600 flex items-center mt-1">
                  <MapPin className="h-4 w-4 mr-1" />
                  {neighborhood.municipalityNl}, {neighborhood.province}
                </p>
              </div>
            </div>
            <div className="flex items-center space-x-3">
              <Button
                variant="ghost"
                icon={isFavorite ? Heart : Heart}
                onClick={toggleFavorite}
                className={isFavorite ? 'text-red-500' : ''}
              >
                {isFavorite ? 'Favoriet' : 'Toevoegen aan favorieten'}
              </Button>
              <Button variant="ghost" icon={Share2} onClick={shareNeighborhood}>
                Delen
              </Button>
              <Button variant="primary" icon={BarChart3}>
                Genereer Scorecard
              </Button>
            </div>
          </div>
        </div>
      </div>

      <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8 py-8">
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Main Content */}
          <div className="lg:col-span-2 space-y-8">
            {/* Overview */}
            <div className="bg-white rounded-lg shadow-sm border border-neutral-200 p-6">
              <h2 className="text-xl font-semibold text-neutral-900 mb-4">Overzicht</h2>
              {neighborhood.description && (
                <p className="text-neutral-700 mb-4">{neighborhood.description}</p>
              )}
              
              <div className="grid grid-cols-1 sm:grid-cols-3 gap-6">
                <div className="text-center">
                  <Users className="h-8 w-8 text-primary-600 mx-auto mb-2" />
                  <div className="text-2xl font-bold text-neutral-900">
                    {neighborhood.population.toLocaleString()}
                  </div>
                  <div className="text-sm text-neutral-600">Inwoners</div>
                </div>
                <div className="text-center">
                  <MapPin className="h-8 w-8 text-primary-600 mx-auto mb-2" />
                  <div className="text-2xl font-bold text-neutral-900">
                    {neighborhood.area.toFixed(1)} km²
                  </div>
                  <div className="text-sm text-neutral-600">Oppervlakte</div>
                </div>
                <div className="text-center">
                  <TrendingUp className="h-8 w-8 text-primary-600 mx-auto mb-2" />
                  <div className="text-2xl font-bold text-neutral-900">
                    {getUrbanizationLabel(neighborhood.urbanizationLevel)}
                  </div>
                  <div className="text-sm text-neutral-600">Type</div>
                </div>
              </div>
            </div>

            {/* Metrics */}
            <div className="bg-white rounded-lg shadow-sm border border-neutral-200 p-6">
              <h2 className="text-xl font-semibold text-neutral-900 mb-6">Buurt Scores</h2>
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
                {Object.entries(neighborhood.metrics).map(([key, value]) => {
                  const Icon = getMetricIcon(key);
                  return (
                    <div key={key} className="flex items-center justify-between p-4 bg-neutral-50 rounded-lg">
                      <div className="flex items-center">
                        <Icon className="h-5 w-5 text-neutral-600 mr-3" />
                        <span className="font-medium text-neutral-900">
                          {getMetricLabel(key)}
                        </span>
                      </div>
                      <div className={`px-3 py-1 rounded-full text-sm font-medium ${getScoreColor(value)}`}>
                        {value.toFixed(1)}/10
                      </div>
                    </div>
                  );
                })}
              </div>
            </div>

            {/* Demographics */}
            <div className="bg-white rounded-lg shadow-sm border border-neutral-200 p-6">
              <h2 className="text-xl font-semibold text-neutral-900 mb-6">Demografie</h2>
              <div className="grid grid-cols-1 sm:grid-cols-3 gap-6">
                <div className="text-center p-4 bg-neutral-50 rounded-lg">
                  <Calendar className="h-6 w-6 text-neutral-600 mx-auto mb-2" />
                  <div className="text-lg font-semibold text-neutral-900">
                    {neighborhood.demographics.averageAge} jaar
                  </div>
                  <div className="text-sm text-neutral-600">Gemiddelde leeftijd</div>
                </div>
                <div className="text-center p-4 bg-neutral-50 rounded-lg">
                  <Users className="h-6 w-6 text-neutral-600 mx-auto mb-2" />
                  <div className="text-lg font-semibold text-neutral-900">
                    {neighborhood.demographics.households.toLocaleString()}
                  </div>
                  <div className="text-sm text-neutral-600">Huishoudens</div>
                </div>
                <div className="text-center p-4 bg-neutral-50 rounded-lg">
                  <Info className="h-6 w-6 text-neutral-600 mx-auto mb-2" />
                  <div className="text-lg font-semibold text-neutral-900">
                    {neighborhood.demographics.foreignerPercentage}%
                  </div>
                  <div className="text-sm text-neutral-600">Buitenlandse inwoners</div>
                </div>
              </div>
            </div>
          </div>

          {/* Sidebar */}
          <div className="space-y-8">
            {/* Map */}
            <div className="bg-white rounded-lg shadow-sm border border-neutral-200 p-6">
              <h3 className="text-lg font-semibold text-neutral-900 mb-4">Locatie</h3>
              <Map
                center={[neighborhood.centroid.latitude, neighborhood.centroid.longitude]}
                zoom={13}
                height="300px"
                markers={[{
                  position: [neighborhood.centroid.latitude, neighborhood.centroid.longitude],
                  popup: neighborhood.nameNl,
                  title: neighborhood.nameNl
                }]}
              />
              <div className="mt-4 text-sm text-neutral-600">
                <p>Lat: {neighborhood.centroid.latitude.toFixed(4)}</p>
                <p>Lng: {neighborhood.centroid.longitude.toFixed(4)}</p>
              </div>
            </div>

            {/* Quick Actions */}
            <div className="bg-white rounded-lg shadow-sm border border-neutral-200 p-6">
              <h3 className="text-lg font-semibold text-neutral-900 mb-4">Acties</h3>
              <div className="space-y-3">
                <Button variant="outline" className="w-full justify-start" icon={BarChart3}>
                  Vergelijk met andere buurten
                </Button>
                <Button variant="outline" className="w-full justify-start" icon={MapPin}>
                  Vind vergelijkbare buurten
                </Button>
                <Button variant="outline" className="w-full justify-start" icon={Share2}>
                  Deel deze buurt
                </Button>
              </div>
            </div>

            {/* Related Info */}
            <div className="bg-primary-50 border border-primary-200 rounded-lg p-6">
              <h3 className="font-semibold text-primary-900 mb-2">
                Wist je dat?
              </h3>
              <p className="text-sm text-primary-800">
                {neighborhood.urbanizationLevel === 'METROPOLITAN' 
                  ? 'Deze buurt is een van de dichtstbevolkte gebieden van België met een rijke geschiedenis.'
                  : 'Deze buurt biedt een perfecte balans tussen stadsleven en rust.'
                }
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}