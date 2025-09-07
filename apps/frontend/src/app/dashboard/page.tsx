'use client';

import React, { useState } from 'react';
import Link from 'next/link';
import { 
  User, 
  Settings, 
  Heart, 
  Search, 
  BarChart3, 
  Map, 
  Clock, 
  TrendingUp,
  Star,
  Calendar,
  FileText,
  Plus
} from 'lucide-react';
import { Button } from '../../components/ui/Button';

export default function DashboardPage() {
  const [user] = useState({
    name: 'Jan Janssen',
    email: 'jan.janssen@example.com',
    avatar: null,
    memberSince: '2024-01-15',
    plan: 'Premium'
  });

  const [recentSearches] = useState([
    { id: 1, query: 'Gent', date: '2024-01-20', results: 12 },
    { id: 2, query: 'Antwerpen centrum', date: '2024-01-19', results: 8 },
    { id: 3, query: 'Leuven', date: '2024-01-18', results: 15 }
  ]);

  const [favorites] = useState([
    { nisCode: '11001', name: 'Centrum - Brussel', municipality: 'Brussel', savedDate: '2024-01-20' },
    { nisCode: '11002', name: 'Etterbeek', municipality: 'Etterbeek', savedDate: '2024-01-19' }
  ]);

  const [stats] = useState({
    totalSearches: 47,
    favoriteNeighborhoods: 12,
    scorecardViews: 23,
    lastActive: '2024-01-20'
  });

  return (
    <div className="min-h-screen bg-neutral-50">
      {/* Header */}
      <div className="bg-white border-b border-neutral-200">
        <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8 py-6">
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-3xl font-bold text-neutral-900">Dashboard</h1>
              <p className="text-neutral-600 mt-1">Welkom terug, {user.name}</p>
            </div>
            <div className="flex items-center space-x-4">
              <Button variant="outline" icon={Settings} href="/dashboard/settings">
                Instellingen
              </Button>
              <Link href="/search">
                <Button variant="primary" icon={Plus}>
                  Nieuwe zoekopdracht
                </Button>
              </Link>
            </div>
          </div>
        </div>
      </div>

      <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8 py-8">
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Main Content */}
          <div className="lg:col-span-2 space-y-8">
            {/* Quick Stats */}
            <div className="grid grid-cols-1 sm:grid-cols-4 gap-4">
              <div className="bg-white p-6 rounded-lg shadow-sm border border-neutral-200">
                <div className="flex items-center">
                  <Search className="h-8 w-8 text-primary-600" />
                  <div className="ml-4">
                    <p className="text-sm font-medium text-neutral-600">Zoekopdrachten</p>
                    <p className="text-2xl font-semibold text-neutral-900">{stats.totalSearches}</p>
                  </div>
                </div>
              </div>
              
              <div className="bg-white p-6 rounded-lg shadow-sm border border-neutral-200">
                <div className="flex items-center">
                  <Heart className="h-8 w-8 text-red-500" />
                  <div className="ml-4">
                    <p className="text-sm font-medium text-neutral-600">Favorieten</p>
                    <p className="text-2xl font-semibold text-neutral-900">{stats.favoriteNeighborhoods}</p>
                  </div>
                </div>
              </div>
              
              <div className="bg-white p-6 rounded-lg shadow-sm border border-neutral-200">
                <div className="flex items-center">
                  <BarChart3 className="h-8 w-8 text-blue-600" />
                  <div className="ml-4">
                    <p className="text-sm font-medium text-neutral-600">Scorecards</p>
                    <p className="text-2xl font-semibold text-neutral-900">{stats.scorecardViews}</p>
                  </div>
                </div>
              </div>
              
              <div className="bg-white p-6 rounded-lg shadow-sm border border-neutral-200">
                <div className="flex items-center">
                  <TrendingUp className="h-8 w-8 text-green-600" />
                  <div className="ml-4">
                    <p className="text-sm font-medium text-neutral-600">Dit jaar</p>
                    <p className="text-2xl font-semibold text-neutral-900">+{stats.totalSearches}</p>
                  </div>
                </div>
              </div>
            </div>

            {/* Recent Searches */}
            <div className="bg-white rounded-lg shadow-sm border border-neutral-200">
              <div className="p-6 border-b border-neutral-200">
                <div className="flex items-center justify-between">
                  <h2 className="text-lg font-semibold text-neutral-900 flex items-center">
                    <Clock className="h-5 w-5 mr-2 text-primary-600" />
                    Recente zoekopdrachten
                  </h2>
                  <Link href="/search">
                    <Button variant="outline" size="sm">
                      Alle bekijken
                    </Button>
                  </Link>
                </div>
              </div>
              <div className="p-6">
                {recentSearches.length === 0 ? (
                  <div className="text-center py-8">
                    <Search className="h-12 w-12 text-neutral-400 mx-auto mb-4" />
                    <p className="text-neutral-600">Nog geen zoekopdrachten uitgevoerd</p>
                    <Link href="/search">
                      <Button variant="primary" className="mt-4">
                        Start je eerste zoekopdracht
                      </Button>
                    </Link>
                  </div>
                ) : (
                  <div className="space-y-4">
                    {recentSearches.map((search) => (
                      <div key={search.id} className="flex items-center justify-between p-3 bg-neutral-50 rounded-lg">
                        <div className="flex items-center">
                          <Map className="h-5 w-5 text-neutral-400 mr-3" />
                          <div>
                            <p className="font-medium text-neutral-900">"{search.query}"</p>
                            <p className="text-sm text-neutral-600">{search.results} resultaten • {search.date}</p>
                          </div>
                        </div>
                        <Button variant="ghost" size="sm">
                          Herhaal zoekopdracht
                        </Button>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            </div>

            {/* Favorite Neighborhoods */}
            <div className="bg-white rounded-lg shadow-sm border border-neutral-200">
              <div className="p-6 border-b border-neutral-200">
                <div className="flex items-center justify-between">
                  <h2 className="text-lg font-semibold text-neutral-900 flex items-center">
                    <Heart className="h-5 w-5 mr-2 text-red-500" />
                    Favoriete buurten
                  </h2>
                  <Link href="/favorites">
                    <Button variant="outline" size="sm">
                      Alle bekijken
                    </Button>
                  </Link>
                </div>
              </div>
              <div className="p-6">
                {favorites.length === 0 ? (
                  <div className="text-center py-8">
                    <Heart className="h-12 w-12 text-neutral-400 mx-auto mb-4" />
                    <p className="text-neutral-600">Nog geen favoriete buurten opgeslagen</p>
                    <Link href="/search">
                      <Button variant="primary" className="mt-4">
                        Ontdek buurten
                      </Button>
                    </Link>
                  </div>
                ) : (
                  <div className="space-y-4">
                    {favorites.map((favorite) => (
                      <div key={favorite.nisCode} className="flex items-center justify-between p-3 bg-neutral-50 rounded-lg">
                        <div className="flex items-center">
                          <Star className="h-5 w-5 text-yellow-500 mr-3" />
                          <div>
                            <p className="font-medium text-neutral-900">{favorite.name}</p>
                            <p className="text-sm text-neutral-600">{favorite.municipality} • Opgeslagen op {favorite.savedDate}</p>
                          </div>
                        </div>
                        <div className="flex space-x-2">
                          <Link href={`/neighborhoods/${favorite.nisCode}`}>
                            <Button variant="ghost" size="sm">
                              Bekijk
                            </Button>
                          </Link>
                        </div>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            </div>
          </div>

          {/* Sidebar */}
          <div className="space-y-6">
            {/* Profile Card */}
            <div className="bg-white rounded-lg shadow-sm border border-neutral-200 p-6">
              <div className="flex items-center mb-4">
                <div className="h-12 w-12 bg-primary-600 rounded-full flex items-center justify-center">
                  <User className="h-6 w-6 text-white" />
                </div>
                <div className="ml-4">
                  <h3 className="font-semibold text-neutral-900">{user.name}</h3>
                  <p className="text-sm text-neutral-600">{user.plan} lidmaatschap</p>
                </div>
              </div>
              <div className="space-y-2 text-sm">
                <div className="flex justify-between">
                  <span className="text-neutral-600">Lid sinds:</span>
                  <span className="text-neutral-900">{new Date(user.memberSince).toLocaleDateString('nl-BE')}</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-neutral-600">Laatste activiteit:</span>
                  <span className="text-neutral-900">{new Date(stats.lastActive).toLocaleDateString('nl-BE')}</span>
                </div>
              </div>
              <div className="mt-4 pt-4 border-t border-neutral-200">
                <Link href="/dashboard/profile">
                  <Button variant="outline" className="w-full">
                    Profiel bewerken
                  </Button>
                </Link>
              </div>
            </div>

            {/* Quick Actions */}
            <div className="bg-white rounded-lg shadow-sm border border-neutral-200 p-6">
              <h3 className="font-semibold text-neutral-900 mb-4">Snelle acties</h3>
              <div className="space-y-3">
                <Link href="/search">
                  <Button variant="ghost" className="w-full justify-start" icon={Search}>
                    Zoek buurten
                  </Button>
                </Link>
                <Link href="/favorites">
                  <Button variant="ghost" className="w-full justify-start" icon={Heart}>
                    Mijn favorieten
                  </Button>
                </Link>
                <Link href="/scorecards">
                  <Button variant="ghost" className="w-full justify-start" icon={BarChart3}>
                    Bekijk scorecards
                  </Button>
                </Link>
                <Link href="/dashboard/settings">
                  <Button variant="ghost" className="w-full justify-start" icon={Settings}>
                    Instellingen
                  </Button>
                </Link>
              </div>
            </div>

            {/* Tips */}
            <div className="bg-primary-50 border border-primary-200 rounded-lg p-6">
              <h3 className="font-semibold text-primary-900 mb-2 flex items-center">
                <FileText className="h-4 w-4 mr-2" />
                Tip van de dag
              </h3>
              <p className="text-sm text-primary-800">
                Gebruik geavanceerde filters in je zoekopdracht om buurten te vinden die perfect bij je levensstijl passen!
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}