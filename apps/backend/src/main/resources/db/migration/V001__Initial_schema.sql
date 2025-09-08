-- Create extensions
CREATE EXTENSION IF NOT EXISTS postgis;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create neighborhoods table
CREATE TABLE neighborhoods (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    municipality VARCHAR(100) NOT NULL,
    province VARCHAR(100) NOT NULL,
    postal_codes TEXT[], -- Array of postal codes
    geometry GEOMETRY(MULTIPOLYGON, 4326), -- Spatial geometry
    population INTEGER,
    area_km2 DECIMAL(10, 4),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    version INTEGER NOT NULL DEFAULT 0
);

-- Create spatial index
CREATE INDEX idx_neighborhoods_geometry ON neighborhoods USING GIST(geometry);
CREATE INDEX idx_neighborhoods_municipality ON neighborhoods(municipality);
CREATE INDEX idx_neighborhoods_postal_codes ON neighborhoods USING GIN(postal_codes);

-- Create addresses table
CREATE TABLE addresses (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    street_name VARCHAR(255) NOT NULL,
    house_number VARCHAR(20) NOT NULL,
    box_number VARCHAR(20),
    postal_code VARCHAR(10) NOT NULL,
    municipality VARCHAR(100) NOT NULL,
    province VARCHAR(100) NOT NULL,
    country VARCHAR(10) NOT NULL DEFAULT 'BE',
    location GEOMETRY(POINT, 4326), -- Spatial point
    neighborhood_id UUID REFERENCES neighborhoods(id),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    version INTEGER NOT NULL DEFAULT 0
);

-- Create spatial index for addresses
CREATE INDEX idx_addresses_location ON addresses USING GIST(location);
CREATE INDEX idx_addresses_postal_code ON addresses(postal_code);
CREATE INDEX idx_addresses_municipality ON addresses(municipality);

-- Create scorecards table
CREATE TABLE scorecards (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    address_id UUID NOT NULL REFERENCES addresses(id),
    
    -- Overall scores (A-E scale converted to 1-5)
    overall_score DECIMAL(3, 2) NOT NULL CHECK (overall_score >= 1 AND overall_score <= 5),
    safety_score DECIMAL(3, 2) NOT NULL CHECK (safety_score >= 1 AND safety_score <= 5),
    mobility_score DECIMAL(3, 2) NOT NULL CHECK (mobility_score >= 1 AND mobility_score <= 5),
    environmental_score DECIMAL(3, 2) NOT NULL CHECK (environmental_score >= 1 AND environmental_score <= 5),
    demographics_score DECIMAL(3, 2) NOT NULL CHECK (demographics_score >= 1 AND demographics_score <= 5),
    infrastructure_score DECIMAL(3, 2) NOT NULL CHECK (infrastructure_score >= 1 AND infrastructure_score <= 5),
    price_trends_score DECIMAL(3, 2) NOT NULL CHECK (price_trends_score >= 1 AND price_trends_score <= 5),
    
    -- Confidence intervals
    confidence_level DECIMAL(3, 2) NOT NULL DEFAULT 0.85,
    
    -- Data freshness
    data_sources TEXT[] NOT NULL, -- Array of data source identifiers
    last_updated_sources TIMESTAMP WITH TIME ZONE NOT NULL,
    
    -- Metadata
    generated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    valid_until TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW() + INTERVAL '24 hours',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    version INTEGER NOT NULL DEFAULT 0
);

CREATE INDEX idx_scorecards_address_id ON scorecards(address_id);
CREATE INDEX idx_scorecards_valid_until ON scorecards(valid_until);
CREATE INDEX idx_scorecards_overall_score ON scorecards(overall_score);

-- Create data_sources table for tracking API data
CREATE TABLE data_sources (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    source_name VARCHAR(100) NOT NULL, -- REIP, Statbel, VMM, etc.
    source_type VARCHAR(50) NOT NULL, -- API, CSV, etc.
    endpoint_url TEXT,
    last_sync_at TIMESTAMP WITH TIME ZONE,
    sync_status VARCHAR(20) DEFAULT 'PENDING', -- PENDING, SUCCESS, FAILED
    error_message TEXT,
    records_processed INTEGER DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX idx_data_sources_name ON data_sources(source_name);

-- Create users table (basic user info, details in Keycloak)
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    keycloak_id UUID NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    preferred_language VARCHAR(5) DEFAULT 'nl', -- nl, fr, en, de
    subscription_tier VARCHAR(20) DEFAULT 'FREE', -- FREE, BASIC, PROFESSIONAL, ENTERPRISE
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    last_login_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    version INTEGER NOT NULL DEFAULT 0
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_keycloak_id ON users(keycloak_id);
CREATE INDEX idx_users_subscription_tier ON users(subscription_tier);

-- Create user_searches table for tracking search history
CREATE TABLE user_searches (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id),
    search_query TEXT NOT NULL,
    address_id UUID REFERENCES addresses(id),
    search_filters JSONB, -- Flexible search parameters
    results_count INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_user_searches_user_id ON user_searches(user_id);
CREATE INDEX idx_user_searches_created_at ON user_searches(created_at);

-- Create reports table for generated PDF reports
CREATE TABLE reports (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID REFERENCES users(id),
    scorecard_id UUID NOT NULL REFERENCES scorecards(id),
    report_type VARCHAR(50) NOT NULL DEFAULT 'STANDARD', -- STANDARD, DETAILED, COMPARISON
    report_format VARCHAR(10) NOT NULL DEFAULT 'PDF', -- PDF, JSON
    file_path TEXT,
    file_size_bytes BIGINT,
    generation_status VARCHAR(20) DEFAULT 'PENDING', -- PENDING, GENERATING, COMPLETED, FAILED
    error_message TEXT,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW() + INTERVAL '30 days',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_reports_user_id ON reports(user_id);
CREATE INDEX idx_reports_scorecard_id ON reports(scorecard_id);
CREATE INDEX idx_reports_expires_at ON reports(expires_at);

-- Create notifications table
CREATE TABLE notifications (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id),
    notification_type VARCHAR(50) NOT NULL, -- ALERT, INFO, WARNING
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    related_entity_type VARCHAR(50), -- ADDRESS, NEIGHBORHOOD, REPORT
    related_entity_id UUID,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    sent_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_notifications_user_id ON notifications(user_id);
CREATE INDEX idx_notifications_is_read ON notifications(is_read);
CREATE INDEX idx_notifications_created_at ON notifications(created_at);

-- Create audit_log table for tracking changes
CREATE TABLE audit_log (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    entity_type VARCHAR(100) NOT NULL,
    entity_id UUID NOT NULL,
    operation VARCHAR(20) NOT NULL, -- CREATE, UPDATE, DELETE
    changed_by_user_id UUID REFERENCES users(id),
    old_values JSONB,
    new_values JSONB,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_audit_log_entity ON audit_log(entity_type, entity_id);
CREATE INDEX idx_audit_log_created_at ON audit_log(created_at);

-- Create function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE 'plpgsql';

-- Create triggers for updated_at
CREATE TRIGGER update_neighborhoods_updated_at BEFORE UPDATE ON neighborhoods FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_addresses_updated_at BEFORE UPDATE ON addresses FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_scorecards_updated_at BEFORE UPDATE ON scorecards FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_reports_updated_at BEFORE UPDATE ON reports FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_data_sources_updated_at BEFORE UPDATE ON data_sources FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();