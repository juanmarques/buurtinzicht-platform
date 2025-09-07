-- Add neighborhoods table with spatial support
CREATE TABLE neighborhoods (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nis_code VARCHAR(10) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    name_nl VARCHAR(255),
    name_fr VARCHAR(255), 
    name_de VARCHAR(255),
    municipality VARCHAR(255) NOT NULL,
    province VARCHAR(255),
    region VARCHAR(255),
    boundary GEOMETRY(MultiPolygon, 4326) NOT NULL,
    centroid GEOMETRY(Point, 4326),
    area_km2 DECIMAL(10,4),
    perimeter_km DECIMAL(10,4),
    population BIGINT,
    population_density DECIMAL(10,2),
    primary_language VARCHAR(5) DEFAULT 'nl',
    postal_codes TEXT,
    elevation_min DECIMAL(8,2),
    elevation_max DECIMAL(8,2),
    elevation_avg DECIMAL(8,2),
    urbanization_level VARCHAR(20) CHECK (urbanization_level IN ('RURAL', 'SUBURBAN', 'URBAN', 'METROPOLITAN')),
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0
);

-- Create indexes for performance
CREATE INDEX idx_neighborhoods_nis_code ON neighborhoods(nis_code);
CREATE INDEX idx_neighborhoods_name ON neighborhoods(name);
CREATE INDEX idx_neighborhoods_municipality ON neighborhoods(municipality);
CREATE INDEX idx_neighborhoods_province ON neighborhoods(province);
CREATE INDEX idx_neighborhoods_region ON neighborhoods(region);
CREATE INDEX idx_neighborhoods_primary_language ON neighborhoods(primary_language);
CREATE INDEX idx_neighborhoods_urbanization_level ON neighborhoods(urbanization_level);
CREATE INDEX idx_neighborhoods_is_active ON neighborhoods(is_active);
CREATE INDEX idx_neighborhoods_population ON neighborhoods(population);

-- Spatial indexes for GIS operations
CREATE INDEX idx_neighborhoods_boundary ON neighborhoods USING GIST(boundary);
CREATE INDEX idx_neighborhoods_centroid ON neighborhoods USING GIST(centroid);

-- Full-text search index for neighborhood names
CREATE INDEX idx_neighborhoods_name_search ON neighborhoods USING GIN(to_tsvector('dutch', name || ' ' || municipality));

-- Update trigger for updated_at
CREATE OR REPLACE FUNCTION update_neighborhoods_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    NEW.version = OLD.version + 1;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER neighborhoods_updated_at_trigger
    BEFORE UPDATE ON neighborhoods
    FOR EACH ROW
    EXECUTE FUNCTION update_neighborhoods_updated_at();

-- Function to calculate and update centroid from boundary
CREATE OR REPLACE FUNCTION update_neighborhood_centroid()
RETURNS TRIGGER AS $$
BEGIN
    -- Calculate centroid from boundary if not provided
    IF NEW.centroid IS NULL AND NEW.boundary IS NOT NULL THEN
        NEW.centroid = ST_Centroid(NEW.boundary);
    END IF;
    
    -- Calculate area if not provided
    IF NEW.area_km2 IS NULL AND NEW.boundary IS NOT NULL THEN
        NEW.area_km2 = ST_Area(ST_Transform(NEW.boundary, 3857)) / 1000000.0;
    END IF;
    
    -- Calculate perimeter if not provided
    IF NEW.perimeter_km IS NULL AND NEW.boundary IS NOT NULL THEN
        NEW.perimeter_km = ST_Perimeter(ST_Transform(NEW.boundary, 3857)) / 1000.0;
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER neighborhoods_centroid_trigger
    BEFORE INSERT OR UPDATE ON neighborhoods
    FOR EACH ROW
    EXECUTE FUNCTION update_neighborhood_centroid();

-- Insert some sample neighborhoods (Belgian municipalities)
INSERT INTO neighborhoods (nis_code, name, name_nl, name_fr, municipality, province, region, boundary, population, primary_language, postal_codes, urbanization_level) VALUES
-- Brussels-Capital Region
('21004', 'Brussels', 'Brussel', 'Bruxelles', 'City of Brussels', 'Brussels-Capital Region', 'Brussels-Capital Region', 
 ST_GeomFromText('MULTIPOLYGON(((4.3320 50.8267, 4.3730 50.8267, 4.3730 50.8650, 4.3320 50.8650, 4.3320 50.8267)))', 4326),
 179277, 'fr', '1000,1020,1120,1130', 'METROPOLITAN'),

-- Antwerp
('11002', 'Antwerp', 'Antwerpen', 'Anvers', 'Antwerp', 'Antwerp', 'Flanders',
 ST_GeomFromText('MULTIPOLYGON(((4.3800 51.1900, 4.4400 51.1900, 4.4400 51.2400, 4.3800 51.2400, 4.3800 51.1900)))', 4326),
 529247, 'nl', '2000,2018,2020,2030,2040,2050,2060,2070,2100,2140,2180', 'METROPOLITAN'),

-- Ghent
('44021', 'Ghent', 'Gent', 'Gand', 'Ghent', 'East Flanders', 'Flanders',
 ST_GeomFromText('MULTIPOLYGON(((3.6800 51.0200, 3.7500 51.0200, 3.7500 51.0800, 3.6800 51.0800, 3.6800 51.0200)))', 4326),
 262219, 'nl', '9000,9030,9031,9032,9040,9041,9042,9050,9051,9052', 'METROPOLITAN'),

-- Charleroi
('52011', 'Charleroi', 'Charleroi', 'Charleroi', 'Charleroi', 'Hainaut', 'Wallonia',
 ST_GeomFromText('MULTIPOLYGON(((4.4000 50.3900, 4.4500 50.3900, 4.4500 50.4300, 4.4000 50.4300, 4.4000 50.3900)))', 4326),
 201816, 'fr', '6000,6001,6010,6020,6030,6031,6032,6040,6041,6042,6043,6044', 'URBAN'),

-- Liège
('62063', 'Liège', 'Luik', 'Liège', 'Liège', 'Liège', 'Wallonia',
 ST_GeomFromText('MULTIPOLYGON(((5.5500 50.6100, 5.6100 50.6100, 5.6100 50.6500, 5.5500 50.6500, 5.5500 50.6100)))', 4326),
 197355, 'fr', '4000,4020,4030,4031,4032', 'URBAN'),

-- Bruges
('31005', 'Bruges', 'Brugge', 'Bruges', 'Bruges', 'West Flanders', 'Flanders',
 ST_GeomFromText('MULTIPOLYGON(((3.1900 51.1900, 3.2500 51.1900, 3.2500 51.2300, 3.1900 51.2300, 3.1900 51.1900)))', 4326),
 118284, 'nl', '8000,8200,8310,8380', 'URBAN'),

-- Namur
('92094', 'Namur', 'Namen', 'Namur', 'Namur', 'Namur', 'Wallonia',
 ST_GeomFromText('MULTIPOLYGON(((4.8400 50.4500, 4.8900 50.4500, 4.8900 50.4800, 4.8400 50.4800, 4.8400 50.4500)))', 4326),
 111439, 'fr', '5000,5001,5002,5003,5004,5020,5021,5022,5024,5030,5031,5032', 'URBAN'),

-- Leuven
('24062', 'Leuven', 'Leuven', 'Louvain', 'Leuven', 'Flemish Brabant', 'Flanders',
 ST_GeomFromText('MULTIPOLYGON(((4.6800 50.8600, 4.7200 50.8600, 4.7200 50.8900, 4.6800 50.8900, 4.6800 50.8600)))', 4326),
 101808, 'nl', '3000,3001,3010,3012,3018', 'URBAN'),

-- Mons
('23064', 'Mons', 'Bergen', 'Mons', 'Mons', 'Hainaut', 'Wallonia',
 ST_GeomFromText('MULTIPOLYGON(((3.9300 50.4400, 3.9700 50.4400, 3.9700 50.4700, 3.9300 50.4700, 3.9300 50.4400)))', 4326),
 95171, 'fr', '7000,7011,7012,7020,7021,7022,7024,7030,7031,7032,7033,7034', 'URBAN'),

-- Aalst
('41002', 'Aalst', 'Aalst', 'Alost', 'Aalst', 'East Flanders', 'Flanders',
 ST_GeomFromText('MULTIPOLYGON(((4.0200 50.9200, 4.0600 50.9200, 4.0600 50.9500, 4.0200 50.9500, 4.0200 50.9200)))', 4326),
 87020, 'nl', '9300,9310,9320,9340,9360,9370,9380,9390,9400', 'SUBURBAN');

-- Update population densities based on calculated areas
UPDATE neighborhoods 
SET population_density = CASE 
    WHEN area_km2 > 0 AND population IS NOT NULL 
    THEN ROUND((population / area_km2)::numeric, 2)
    ELSE NULL 
END
WHERE area_km2 IS NOT NULL AND population IS NOT NULL;

-- Add some elevation data (sample values for Belgian municipalities)
UPDATE neighborhoods SET 
    elevation_min = CASE nis_code
        WHEN '21004' THEN 15.0  -- Brussels
        WHEN '11002' THEN 2.0   -- Antwerp
        WHEN '44021' THEN 5.0   -- Ghent
        WHEN '52011' THEN 120.0 -- Charleroi
        WHEN '62063' THEN 60.0  -- Liège
        WHEN '31005' THEN 1.0   -- Bruges
        WHEN '92094' THEN 85.0  -- Namur
        WHEN '24062' THEN 18.0  -- Leuven
        WHEN '23064' THEN 56.0  -- Mons
        WHEN '41002' THEN 7.0   -- Aalst
        ELSE NULL
    END,
    elevation_max = CASE nis_code
        WHEN '21004' THEN 125.0 -- Brussels
        WHEN '11002' THEN 57.0  -- Antwerp
        WHEN '44021' THEN 35.0  -- Ghent
        WHEN '52011' THEN 220.0 -- Charleroi
        WHEN '62063' THEN 340.0 -- Liège
        WHEN '31005' THEN 28.0  -- Bruges
        WHEN '92094' THEN 265.0 -- Namur
        WHEN '24062' THEN 105.0 -- Leuven
        WHEN '23064' THEN 168.0 -- Mons
        WHEN '41002' THEN 45.0  -- Aalst
        ELSE NULL
    END,
    elevation_avg = CASE nis_code
        WHEN '21004' THEN 57.0  -- Brussels
        WHEN '11002' THEN 12.0  -- Antwerp
        WHEN '44021' THEN 18.0  -- Ghent
        WHEN '52011' THEN 165.0 -- Charleroi
        WHEN '62063' THEN 180.0 -- Liège
        WHEN '31005' THEN 8.0   -- Bruges
        WHEN '92094' THEN 155.0 -- Namur
        WHEN '24062' THEN 48.0  -- Leuven
        WHEN '23064' THEN 98.0  -- Mons
        WHEN '41002' THEN 22.0  -- Aalst
        ELSE NULL
    END
WHERE nis_code IN ('21004', '11002', '44021', '52011', '62063', '31005', '92094', '24062', '23064', '41002');

COMMIT;