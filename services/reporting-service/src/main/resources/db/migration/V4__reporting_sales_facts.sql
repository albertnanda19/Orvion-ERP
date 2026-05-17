CREATE TABLE reporting_sales_facts (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    period VARCHAR(10) NOT NULL,
    total_orders BIGINT,
    total_revenue DECIMAL(18,2),
    conversion_rate DECIMAL(10,4),
    avg_order_value DECIMAL(18,2),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

CREATE INDEX idx_sales_fact_tenant_period ON reporting_sales_facts(tenant_id, period);
