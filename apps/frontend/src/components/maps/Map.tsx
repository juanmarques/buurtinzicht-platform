'use client';

import React, { useEffect, useRef } from 'react';
import { MapContainer, TileLayer, Marker, Popup, useMap } from 'react-leaflet';
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';
import { cn } from '../../lib/utils';

// Fix for default markers in React-Leaflet
delete (L.Icon.Default.prototype as any)._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon-2x.png',
  iconUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon.png',
  shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-shadow.png',
});

export interface MapProps {
  center?: [number, number];
  zoom?: number;
  className?: string;
  height?: string;
  onMapClick?: (lat: number, lng: number) => void;
  markers?: Array<{
    position: [number, number];
    popup?: string;
    title?: string;
  }>;
  polygons?: Array<{
    positions: [number, number][];
    color?: string;
    fillColor?: string;
    fillOpacity?: number;
    popup?: string;
  }>;
}

// Component to handle map click events
function MapClickHandler({ onMapClick }: { onMapClick?: (lat: number, lng: number) => void }) {
  const map = useMap();

  useEffect(() => {
    if (!onMapClick) return;

    const handleClick = (e: L.LeafletMouseEvent) => {
      onMapClick(e.latlng.lat, e.latlng.lng);
    };

    map.on('click', handleClick);

    return () => {
      map.off('click', handleClick);
    };
  }, [map, onMapClick]);

  return null;
}

// Component to handle polygons
function PolygonLayer({ 
  polygons 
}: { 
  polygons?: Array<{
    positions: [number, number][];
    color?: string;
    fillColor?: string;
    fillOpacity?: number;
    popup?: string;
  }>;
}) {
  const map = useMap();

  useEffect(() => {
    if (!polygons || polygons.length === 0) return;

    const polygonLayers: L.Polygon[] = [];

    polygons.forEach((polygon) => {
      const leafletPolygon = L.polygon(polygon.positions, {
        color: polygon.color || '#3388ff',
        fillColor: polygon.fillColor || '#3388ff',
        fillOpacity: polygon.fillOpacity || 0.2,
        weight: 2,
      });

      if (polygon.popup) {
        leafletPolygon.bindPopup(polygon.popup);
      }

      leafletPolygon.addTo(map);
      polygonLayers.push(leafletPolygon);
    });

    return () => {
      polygonLayers.forEach(layer => map.removeLayer(layer));
    };
  }, [map, polygons]);

  return null;
}

export function Map({ 
  center = [50.8503, 4.3517], // Brussels coordinates
  zoom = 13,
  className,
  height = '400px',
  onMapClick,
  markers = [],
  polygons = [],
}: MapProps) {
  const mapRef = useRef<L.Map | null>(null);

  return (
    <div className={cn('relative overflow-hidden rounded-lg border', className)} style={{ height }}>
      <MapContainer
        center={center}
        zoom={zoom}
        style={{ height: '100%', width: '100%' }}
        ref={mapRef}
      >
        <TileLayer
          attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
          url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        />
        
        {/* Render markers */}
        {markers.map((marker, index) => (
          <Marker key={index} position={marker.position}>
            {marker.popup && <Popup>{marker.popup}</Popup>}
          </Marker>
        ))}

        {/* Handle map clicks */}
        <MapClickHandler onMapClick={onMapClick} />

        {/* Render polygons */}
        <PolygonLayer polygons={polygons} />
      </MapContainer>
    </div>
  );
}