CREATE TABLE reporting_finance_facts (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    period VARCHAR(10) NOT NULL,
    revenue DECIMAL(18,2),
    expenses DECIMAL(18,2),
    net_profit DECIMAL(18,2),
    gross_margin DECIMAL(18,2),
    invoice_count BIGINT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

CREATE INDEX idx_fin_fact_tenant_period ON reporting_finance_facts(tenant_id, period);
