CREATE TABLE opportunities (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    lead_id UUID,
    account_id VARCHAR(100),
    assigned_to VARCHAR(100),
    stage VARCHAR(50) NOT NULL,
    probability INT DEFAULT 0,
    expected_value_amount DECIMAL(19,4),
    expected_value_currency VARCHAR(3) DEFAULT 'IDR',
    expected_close_date TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE TABLE activities (
    id UUID PRIMARY KEY,
    opportunity_id UUID NOT NULL REFERENCES opportunities(id),
    type VARCHAR(50) NOT NULL,
    scheduled_at TIMESTAMP WITH TIME ZONE,
    completed_at TIMESTAMP WITH TIME ZONE,
    outcome TEXT,
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE INDEX idx_opp_tenant_stage ON opportunities(tenant_id, stage);
CREATE INDEX idx_opp_tenant_assigned ON opportunities(tenant_id, assigned_to);
CREATE INDEX idx_activities_opp ON activities(opportunity_id);
