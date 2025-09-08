-- Add neighborhood scorecards table
CREATE TABLE neighborhood_scorecards (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    neighborhood_id UUID NOT NULL,
    overall_score DECIMAL(4,2) NOT NULL CHECK (overall_score >= 0.0 AND overall_score <= 100.0),
    
    -- Infrastructure Metrics
    transportation_score DECIMAL(4,2) CHECK (transportation_score >= 0.0 AND transportation_score <= 100.0),
    public_services_score DECIMAL(4,2) CHECK (public_services_score >= 0.0 AND public_services_score <= 100.0),
    connectivity_score DECIMAL(4,2) CHECK (connectivity_score >= 0.0 AND connectivity_score <= 100.0),
    
    -- Economic Metrics
    cost_of_living_score DECIMAL(4,2) CHECK (cost_of_living_score >= 0.0 AND cost_of_living_score <= 100.0),
    employment_score DECIMAL(4,2) CHECK (employment_score >= 0.0 AND employment_score <= 100.0),
    housing_market_score DECIMAL(4,2) CHECK (housing_market_score >= 0.0 AND housing_market_score <= 100.0),
    
    -- Social & Cultural Metrics
    education_score DECIMAL(4,2) CHECK (education_score >= 0.0 AND education_score <= 100.0),
    healthcare_score DECIMAL(4,2) CHECK (healthcare_score >= 0.0 AND healthcare_score <= 100.0),
    cultural_amenities_score DECIMAL(4,2) CHECK (cultural_amenities_score >= 0.0 AND cultural_amenities_score <= 100.0),
    community_engagement_score DECIMAL(4,2) CHECK (community_engagement_score >= 0.0 AND community_engagement_score <= 100.0),
    
    -- Environmental Metrics
    air_quality_score DECIMAL(4,2) CHECK (air_quality_score >= 0.0 AND air_quality_score <= 100.0),
    green_spaces_score DECIMAL(4,2) CHECK (green_spaces_score >= 0.0 AND green_spaces_score <= 100.0),
    noise_pollution_score DECIMAL(4,2) CHECK (noise_pollution_score >= 0.0 AND noise_pollution_score <= 100.0),
    
    -- Safety & Security Metrics
    crime_safety_score DECIMAL(4,2) CHECK (crime_safety_score >= 0.0 AND crime_safety_score <= 100.0),
    emergency_services_score DECIMAL(4,2) CHECK (emergency_services_score >= 0.0 AND emergency_services_score <= 100.0),
    
    -- Data Quality & Confidence
    data_completeness_percentage DECIMAL(5,2) CHECK (data_completeness_percentage >= 0.0 AND data_completeness_percentage <= 100.0),
    confidence_level DECIMAL(4,2) CHECK (confidence_level >= 0.0 AND confidence_level <= 100.0),
    data_sources_count INTEGER CHECK (data_sources_count >= 0),
    
    -- Versioning and Metadata
    scorecard_version VARCHAR(20) NOT NULL CHECK (scorecard_version ~ '^\\d+\\.\\d+\\.\\d+$'),
    calculation_algorithm VARCHAR(100) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT true,
    is_published BOOLEAN NOT NULL DEFAULT false,
    language_code VARCHAR(5) DEFAULT 'nl' CHECK (language_code IN ('nl', 'fr', 'de', 'en')),
    
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    
    -- Foreign key constraint
    CONSTRAINT fk_scorecard_neighborhood FOREIGN KEY (neighborhood_id) REFERENCES neighborhoods(id) ON DELETE CASCADE
);

-- Create indexes for performance
CREATE INDEX idx_scorecards_neighborhood_id ON neighborhood_scorecards(neighborhood_id);
CREATE INDEX idx_scorecards_overall_score ON neighborhood_scorecards(overall_score);
CREATE INDEX idx_scorecards_is_active ON neighborhood_scorecards(is_active);
CREATE INDEX idx_scorecards_is_published ON neighborhood_scorecards(is_published);
CREATE INDEX idx_scorecards_language_code ON neighborhood_scorecards(language_code);
CREATE INDEX idx_scorecards_scorecard_version ON neighborhood_scorecards(scorecard_version);
CREATE INDEX idx_scorecards_created_at ON neighborhood_scorecards(created_at);

-- Composite index for common queries
CREATE INDEX idx_scorecards_active_published_lang ON neighborhood_scorecards(is_active, is_published, language_code);

-- Update trigger for updated_at and version
CREATE OR REPLACE FUNCTION update_scorecard_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    NEW.version = OLD.version + 1;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER scorecards_updated_at_trigger
    BEFORE UPDATE ON neighborhood_scorecards
    FOR EACH ROW
    EXECUTE FUNCTION update_scorecard_updated_at();

-- Insert sample scorecard data for existing neighborhoods
INSERT INTO neighborhood_scorecards (
    neighborhood_id, overall_score, scorecard_version, calculation_algorithm,
    transportation_score, public_services_score, connectivity_score,
    cost_of_living_score, employment_score, housing_market_score,
    education_score, healthcare_score, cultural_amenities_score, community_engagement_score,
    air_quality_score, green_spaces_score, noise_pollution_score,
    crime_safety_score, emergency_services_score,
    data_completeness_percentage, confidence_level, data_sources_count,
    is_published, language_code
)
SELECT 
    n.id as neighborhood_id,
    -- Overall scores based on urbanization and region
    CASE 
        WHEN n.name LIKE '%Brussels%' THEN 78.5
        WHEN n.name LIKE '%Antwerp%' THEN 76.2
        WHEN n.name LIKE '%Ghent%' THEN 74.8
        WHEN n.name LIKE '%Bruges%' THEN 73.1
        WHEN n.name LIKE '%Leuven%' THEN 75.9
        WHEN n.name LIKE '%Liège%' THEN 71.4
        WHEN n.name LIKE '%Charleroi%' THEN 68.7
        WHEN n.name LIKE '%Namur%' THEN 70.3
        WHEN n.name LIKE '%Mons%' THEN 69.1
        WHEN n.name LIKE '%Aalst%' THEN 72.6
        ELSE 70.0
    END as overall_score,
    '1.0.0' as scorecard_version,
    'WEIGHTED_AVERAGE_V1' as calculation_algorithm,
    
    -- Transportation scores (higher for major cities)
    CASE 
        WHEN n.urbanization_level = 'METROPOLITAN' THEN 85.0
        WHEN n.urbanization_level = 'URBAN' THEN 75.0
        WHEN n.urbanization_level = 'SUBURBAN' THEN 65.0
        ELSE 55.0
    END as transportation_score,
    
    -- Public services (better in major cities and Brussels region)
    CASE 
        WHEN n.region = 'Brussels-Capital Region' THEN 88.0
        WHEN n.urbanization_level = 'METROPOLITAN' THEN 82.0
        WHEN n.urbanization_level = 'URBAN' THEN 78.0
        ELSE 72.0
    END as public_services_score,
    
    -- Connectivity (Belgium has generally good connectivity)
    CASE 
        WHEN n.name LIKE '%Brussels%' OR n.name LIKE '%Antwerp%' THEN 95.0
        WHEN n.urbanization_level = 'METROPOLITAN' THEN 88.0
        WHEN n.urbanization_level = 'URBAN' THEN 83.0
        ELSE 75.0
    END as connectivity_score,
    
    -- Cost of living (higher score = more affordable)
    CASE 
        WHEN n.name LIKE '%Brussels%' THEN 55.0
        WHEN n.urbanization_level = 'METROPOLITAN' THEN 60.0
        WHEN n.urbanization_level = 'URBAN' THEN 68.0
        WHEN n.urbanization_level = 'SUBURBAN' THEN 75.0
        ELSE 80.0
    END as cost_of_living_score,
    
    -- Employment opportunities
    CASE 
        WHEN n.region = 'Brussels-Capital Region' THEN 85.0
        WHEN n.name LIKE '%Antwerp%' THEN 82.0
        WHEN n.region LIKE '%Flanders%' THEN 78.0
        ELSE 72.0
    END as employment_score,
    
    -- Housing market stability
    CASE 
        WHEN n.urbanization_level = 'SUBURBAN' THEN 75.0
        WHEN n.urbanization_level = 'URBAN' THEN 68.0
        WHEN n.urbanization_level = 'METROPOLITAN' THEN 62.0
        ELSE 70.0
    END as housing_market_score,
    
    -- Education (Belgium has excellent education)
    CASE 
        WHEN n.name LIKE '%Leuven%' OR n.name LIKE '%Ghent%' OR n.name LIKE '%Brussels%' OR n.name LIKE '%Liège%' THEN 88.0
        WHEN n.urbanization_level = 'METROPOLITAN' THEN 85.0
        WHEN n.urbanization_level = 'URBAN' THEN 82.0
        ELSE 78.0
    END as education_score,
    
    -- Healthcare (excellent in Belgium)
    CASE 
        WHEN n.name LIKE '%Brussels%' OR n.name LIKE '%Antwerp%' OR n.name LIKE '%Liège%' THEN 92.0
        WHEN n.urbanization_level = 'METROPOLITAN' THEN 88.0
        WHEN n.urbanization_level = 'URBAN' THEN 85.0
        ELSE 80.0
    END as healthcare_score,
    
    -- Cultural amenities
    CASE 
        WHEN n.name LIKE '%Brussels%' OR n.name LIKE '%Bruges%' OR n.name LIKE '%Ghent%' OR n.name LIKE '%Antwerp%' THEN 90.0
        WHEN n.urbanization_level = 'METROPOLITAN' THEN 78.0
        WHEN n.urbanization_level = 'URBAN' THEN 70.0
        ELSE 55.0
    END as cultural_amenities_score,
    
    -- Community engagement
    CASE 
        WHEN n.urbanization_level = 'SUBURBAN' OR n.urbanization_level = 'RURAL' THEN 78.0
        WHEN n.region LIKE '%Flanders%' THEN 75.0
        ELSE 70.0
    END as community_engagement_score,
    
    -- Air quality (lower in industrial cities)
    CASE 
        WHEN n.name LIKE '%Antwerp%' OR n.name LIKE '%Charleroi%' THEN 65.0
        WHEN n.name LIKE '%Brussels%' THEN 70.0
        WHEN n.urbanization_level = 'RURAL' THEN 85.0
        WHEN n.urbanization_level = 'SUBURBAN' THEN 78.0
        ELSE 72.0
    END as air_quality_score,
    
    -- Green spaces
    CASE 
        WHEN n.name LIKE '%Brussels%' THEN 78.0 -- Brussels has good parks
        WHEN n.urbanization_level = 'RURAL' THEN 88.0
        WHEN n.urbanization_level = 'SUBURBAN' THEN 75.0
        WHEN n.urbanization_level = 'URBAN' THEN 68.0
        ELSE 60.0
    END as green_spaces_score,
    
    -- Noise pollution (higher score = less noise)
    CASE 
        WHEN n.urbanization_level = 'RURAL' THEN 85.0
        WHEN n.urbanization_level = 'SUBURBAN' THEN 75.0
        WHEN n.urbanization_level = 'URBAN' THEN 65.0
        WHEN n.name LIKE '%Brussels%' OR n.name LIKE '%Antwerp%' THEN 55.0
        ELSE 70.0
    END as noise_pollution_score,
    
    -- Crime safety (Belgium is generally safe)
    CASE 
        WHEN n.name LIKE '%Brussels%' THEN 75.0
        WHEN n.name LIKE '%Charleroi%' THEN 70.0
        WHEN n.urbanization_level = 'SUBURBAN' OR n.urbanization_level = 'RURAL' THEN 85.0
        ELSE 80.0
    END as crime_safety_score,
    
    -- Emergency services (excellent in Belgium)
    CASE 
        WHEN n.urbanization_level = 'METROPOLITAN' THEN 92.0
        WHEN n.urbanization_level = 'URBAN' THEN 88.0
        WHEN n.urbanization_level = 'SUBURBAN' THEN 85.0
        ELSE 80.0
    END as emergency_services_score,
    
    -- Data completeness and confidence
    85.0 + (RANDOM() * 10) as data_completeness_percentage, -- 85-95%
    88.0 + (RANDOM() * 8) as confidence_level, -- 88-96%
    8 + FLOOR(RANDOM() * 5) as data_sources_count, -- 8-12 sources
    
    true as is_published,
    'nl' as language_code

FROM neighborhoods n 
WHERE n.is_active = true;

-- Create French language variants for major cities
INSERT INTO neighborhood_scorecards (
    neighborhood_id, overall_score, scorecard_version, calculation_algorithm,
    transportation_score, public_services_score, connectivity_score,
    cost_of_living_score, employment_score, housing_market_score,
    education_score, healthcare_score, cultural_amenities_score, community_engagement_score,
    air_quality_score, green_spaces_score, noise_pollution_score,
    crime_safety_score, emergency_services_score,
    data_completeness_percentage, confidence_level, data_sources_count,
    is_published, language_code
)
SELECT 
    neighborhood_id, overall_score, scorecard_version, calculation_algorithm,
    transportation_score, public_services_score, connectivity_score,
    cost_of_living_score, employment_score, housing_market_score,
    education_score, healthcare_score, cultural_amenities_score, community_engagement_score,
    air_quality_score, green_spaces_score, noise_pollution_score,
    crime_safety_score, emergency_services_score,
    data_completeness_percentage, confidence_level, data_sources_count,
    is_published, 'fr' as language_code
FROM neighborhood_scorecards 
WHERE language_code = 'nl' 
AND neighborhood_id IN (
    SELECT id FROM neighborhoods 
    WHERE region = 'Brussels-Capital Region' OR region LIKE '%Wallonia%'
);

-- Create English language variants for major international cities
INSERT INTO neighborhood_scorecards (
    neighborhood_id, overall_score, scorecard_version, calculation_algorithm,
    transportation_score, public_services_score, connectivity_score,
    cost_of_living_score, employment_score, housing_market_score,
    education_score, healthcare_score, cultural_amenities_score, community_engagement_score,
    air_quality_score, green_spaces_score, noise_pollution_score,
    crime_safety_score, emergency_services_score,
    data_completeness_percentage, confidence_level, data_sources_count,
    is_published, language_code
)
SELECT 
    neighborhood_id, overall_score, scorecard_version, calculation_algorithm,
    transportation_score, public_services_score, connectivity_score,
    cost_of_living_score, employment_score, housing_market_score,
    education_score, healthcare_score, cultural_amenities_score, community_engagement_score,
    air_quality_score, green_spaces_score, noise_pollution_score,
    crime_safety_score, emergency_services_score,
    data_completeness_percentage, confidence_level, data_sources_count,
    is_published, 'en' as language_code
FROM neighborhood_scorecards 
WHERE language_code = 'nl' 
AND neighborhood_id IN (
    SELECT id FROM neighborhoods 
    WHERE name IN ('Brussels', 'Antwerp', 'Ghent', 'Bruges')
);

COMMIT;