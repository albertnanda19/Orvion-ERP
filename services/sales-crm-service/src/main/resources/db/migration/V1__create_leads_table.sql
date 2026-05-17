CREATE TABLE leads (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255),
    phone VARCHAR(50),
    company VARCHAR(200),
    source VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    assigned_to VARCHAR(100),
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE INDEX idx_leads_tenant_status ON leads(tenant_id, status);
CREATE INDEX idx_leads_tenant_source ON leads(tenant_id, source);
CREATE INDEX idx_leads_assigned ON leads(tenant_id, assigned_to);
