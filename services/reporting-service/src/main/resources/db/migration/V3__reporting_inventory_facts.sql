CREATE TABLE reporting_inventory_facts (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    period VARCHAR(10) NOT NULL,
    total_products BIGINT,
    total_stock_value DECIMAL(18,2),
    low_stock_count BIGINT,
    turnover_rate DECIMAL(10,4),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

CREATE INDEX idx_inv_fact_tenant_period ON reporting_inventory_facts(tenant_id, period);
