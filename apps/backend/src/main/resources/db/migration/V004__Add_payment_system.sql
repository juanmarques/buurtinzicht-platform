-- Add subscription plans table
CREATE TABLE subscription_plans (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    description TEXT,
    plan_type VARCHAR(20) NOT NULL CHECK (plan_type IN ('FREE', 'B2C_BASIC', 'B2C_PREMIUM', 'B2B_STARTER', 'B2B_PROFESSIONAL', 'B2B_ENTERPRISE')),
    billing_interval VARCHAR(20) NOT NULL CHECK (billing_interval IN ('MONTHLY', 'QUARTERLY', 'YEARLY', 'ONE_TIME')),
    price DECIMAL(10,2) NOT NULL CHECK (price >= 0.00),
    currency VARCHAR(3) NOT NULL DEFAULT 'EUR' CHECK (currency ~ '^[A-Z]{3}$'),
    stripe_price_id VARCHAR(100),
    stripe_product_id VARCHAR(100),
    
    -- Feature Limits
    max_scorecard_requests_per_month INTEGER CHECK (max_scorecard_requests_per_month >= 0),
    max_api_calls_per_day INTEGER CHECK (max_api_calls_per_day >= 0),
    includes_premium_features BOOLEAN NOT NULL DEFAULT false,
    includes_historic_data BOOLEAN NOT NULL DEFAULT false,
    includes_comparisons BOOLEAN NOT NULL DEFAULT false,
    includes_api_access BOOLEAN NOT NULL DEFAULT false,
    includes_bulk_exports BOOLEAN NOT NULL DEFAULT false,
    includes_priority_support BOOLEAN NOT NULL DEFAULT false,
    
    trial_period_days INTEGER CHECK (trial_period_days >= 0) DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT true,
    is_featured BOOLEAN NOT NULL DEFAULT false,
    sort_order INTEGER CHECK (sort_order >= 0) DEFAULT 0,
    
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0
);

-- Add user subscriptions table
CREATE TABLE user_subscriptions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id VARCHAR(255) NOT NULL,
    subscription_plan_id UUID NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('ACTIVE', 'TRIALING', 'PAST_DUE', 'CANCELED', 'UNPAID', 'INCOMPLETE', 'INCOMPLETE_EXPIRED', 'PAUSED')),
    stripe_subscription_id VARCHAR(100),
    stripe_customer_id VARCHAR(100),
    
    current_period_start TIMESTAMP WITH TIME ZONE,
    current_period_end TIMESTAMP WITH TIME ZONE,
    trial_start TIMESTAMP WITH TIME ZONE,
    trial_end TIMESTAMP WITH TIME ZONE,
    canceled_at TIMESTAMP WITH TIME ZONE,
    ends_at TIMESTAMP WITH TIME ZONE,
    auto_renew BOOLEAN NOT NULL DEFAULT true,
    
    -- Usage tracking
    scorecard_requests_this_month INTEGER CHECK (scorecard_requests_this_month >= 0) DEFAULT 0,
    api_calls_today INTEGER CHECK (api_calls_today >= 0) DEFAULT 0,
    last_usage_reset TIMESTAMP WITH TIME ZONE,
    
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    
    CONSTRAINT fk_subscription_plan FOREIGN KEY (subscription_plan_id) REFERENCES subscription_plans(id) ON DELETE RESTRICT
);

-- Add payment transactions table
CREATE TABLE payment_transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_subscription_id UUID NOT NULL,
    stripe_payment_intent_id VARCHAR(100),
    stripe_charge_id VARCHAR(100),
    transaction_type VARCHAR(20) NOT NULL CHECK (transaction_type IN ('SUBSCRIPTION_PAYMENT', 'SUBSCRIPTION_REFUND', 'ONE_TIME_PAYMENT', 'SETUP_FEE', 'CREDIT', 'CHARGEBACK')),
    status VARCHAR(20) NOT NULL CHECK (status IN ('PENDING', 'PROCESSING', 'SUCCEEDED', 'FAILED', 'CANCELED', 'REFUNDED', 'PARTIALLY_REFUNDED', 'DISPUTED')),
    amount DECIMAL(10,2) NOT NULL CHECK (amount >= 0.00),
    currency VARCHAR(3) NOT NULL DEFAULT 'EUR' CHECK (currency ~ '^[A-Z]{3}$'),
    payment_method VARCHAR(50),
    description VARCHAR(255),
    failure_reason VARCHAR(255),
    processed_at TIMESTAMP WITH TIME ZONE,
    refunded_at TIMESTAMP WITH TIME ZONE,
    refunded_amount DECIMAL(10,2) CHECK (refunded_amount >= 0.00),
    metadata TEXT,
    
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    
    CONSTRAINT fk_payment_user_subscription FOREIGN KEY (user_subscription_id) REFERENCES user_subscriptions(id) ON DELETE CASCADE
);

-- Create indexes for performance
CREATE INDEX idx_subscription_plans_active ON subscription_plans(is_active);
CREATE INDEX idx_subscription_plans_plan_type ON subscription_plans(plan_type);
CREATE INDEX idx_subscription_plans_sort_order ON subscription_plans(sort_order);
CREATE INDEX idx_subscription_plans_stripe_price_id ON subscription_plans(stripe_price_id);

CREATE INDEX idx_user_subscriptions_user_id ON user_subscriptions(user_id);
CREATE INDEX idx_user_subscriptions_status ON user_subscriptions(status);
CREATE INDEX idx_user_subscriptions_stripe_subscription_id ON user_subscriptions(stripe_subscription_id);
CREATE INDEX idx_user_subscriptions_stripe_customer_id ON user_subscriptions(stripe_customer_id);
CREATE INDEX idx_user_subscriptions_current_period_end ON user_subscriptions(current_period_end);
CREATE INDEX idx_user_subscriptions_trial_end ON user_subscriptions(trial_end);
CREATE UNIQUE INDEX idx_user_subscriptions_active_user ON user_subscriptions(user_id) WHERE status IN ('ACTIVE', 'TRIALING');

CREATE INDEX idx_payment_transactions_subscription_id ON payment_transactions(user_subscription_id);
CREATE INDEX idx_payment_transactions_status ON payment_transactions(status);
CREATE INDEX idx_payment_transactions_transaction_type ON payment_transactions(transaction_type);
CREATE INDEX idx_payment_transactions_stripe_payment_intent ON payment_transactions(stripe_payment_intent_id);
CREATE INDEX idx_payment_transactions_created_at ON payment_transactions(created_at);

-- Update triggers for updated_at and version
CREATE OR REPLACE FUNCTION update_subscription_plans_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    NEW.version = OLD.version + 1;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER subscription_plans_updated_at_trigger
    BEFORE UPDATE ON subscription_plans
    FOR EACH ROW
    EXECUTE FUNCTION update_subscription_plans_updated_at();

CREATE OR REPLACE FUNCTION update_user_subscriptions_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    NEW.version = OLD.version + 1;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER user_subscriptions_updated_at_trigger
    BEFORE UPDATE ON user_subscriptions
    FOR EACH ROW
    EXECUTE FUNCTION update_user_subscriptions_updated_at();

CREATE OR REPLACE FUNCTION update_payment_transactions_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    NEW.version = OLD.version + 1;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER payment_transactions_updated_at_trigger
    BEFORE UPDATE ON payment_transactions
    FOR EACH ROW
    EXECUTE FUNCTION update_payment_transactions_updated_at();

-- Insert default subscription plans
INSERT INTO subscription_plans (name, description, plan_type, billing_interval, price, 
    max_scorecard_requests_per_month, max_api_calls_per_day, 
    includes_premium_features, includes_historic_data, includes_comparisons, 
    includes_api_access, includes_bulk_exports, includes_priority_support,
    trial_period_days, is_featured, sort_order) VALUES

-- Free Plan
('Free', 'Perfect for trying out Buurtinzicht with basic neighborhood insights', 
 'FREE', 'MONTHLY', 0.00, 5, 50, false, false, false, false, false, false, 0, false, 1),

-- B2C Plans
('Basic', 'Essential neighborhood insights for homebuyers and renters', 
 'B2C_BASIC', 'MONTHLY', 9.99, 25, 200, false, false, true, false, false, false, 7, false, 2),

('Premium', 'Complete neighborhood analysis with historic trends and comparisons', 
 'B2C_PREMIUM', 'MONTHLY', 19.99, 100, 500, true, true, true, false, true, false, 14, true, 3),

-- B2B Plans  
('Starter', 'For small businesses and real estate professionals', 
 'B2B_STARTER', 'MONTHLY', 49.99, 500, 2000, true, true, true, true, true, false, 14, false, 4),

('Professional', 'For growing businesses with team collaboration needs', 
 'B2B_PROFESSIONAL', 'MONTHLY', 99.99, 2000, 5000, true, true, true, true, true, true, 14, true, 5),

('Enterprise', 'For large organizations with unlimited access and priority support', 
 'B2B_ENTERPRISE', 'MONTHLY', 299.99, NULL, NULL, true, true, true, true, true, true, 30, false, 6);

-- Insert yearly variants with discounts
INSERT INTO subscription_plans (name, description, plan_type, billing_interval, price, 
    max_scorecard_requests_per_month, max_api_calls_per_day, 
    includes_premium_features, includes_historic_data, includes_comparisons, 
    includes_api_access, includes_bulk_exports, includes_priority_support,
    trial_period_days, is_featured, sort_order) VALUES

('Basic (Yearly)', 'Essential neighborhood insights - Save 20% with yearly billing', 
 'B2C_BASIC', 'YEARLY', 95.90, 25, 200, false, false, true, false, false, false, 7, false, 12),

('Premium (Yearly)', 'Complete neighborhood analysis - Save 20% with yearly billing', 
 'B2C_PREMIUM', 'YEARLY', 191.90, 100, 500, true, true, true, false, true, false, 14, false, 13),

('Starter (Yearly)', 'For small businesses - Save 20% with yearly billing', 
 'B2B_STARTER', 'YEARLY', 479.90, 500, 2000, true, true, true, true, true, false, 14, false, 14),

('Professional (Yearly)', 'For growing businesses - Save 20% with yearly billing', 
 'B2B_PROFESSIONAL', 'YEARLY', 959.90, 2000, 5000, true, true, true, true, true, true, 14, false, 15),

('Enterprise (Yearly)', 'For large organizations - Save 20% with yearly billing', 
 'B2B_ENTERPRISE', 'YEARLY', 2879.90, NULL, NULL, true, true, true, true, true, true, 30, false, 16);

COMMIT;