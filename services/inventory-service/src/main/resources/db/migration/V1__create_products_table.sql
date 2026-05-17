CREATE TABLE products (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    sku VARCHAR(50) NOT NULL,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    category VARCHAR(100),
    unit VARCHAR(20) NOT NULL,
    current_stock NUMERIC(18,4) NOT NULL DEFAULT 0,
    reserved_stock NUMERIC(18,4) NOT NULL DEFAULT 0,
    reorder_point NUMERIC(18,4) NOT NULL DEFAULT 0,
    reorder_quantity NUMERIC(18,4) NOT NULL DEFAULT 0,
    preferred_supplier_id VARCHAR(50),
    warehouse_id VARCHAR(50),
    standard_cost NUMERIC(18,4),
    cost_currency VARCHAR(3),
    costing_method VARCHAR(20) NOT NULL DEFAULT 'AVERAGE_COST',
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT uq_prod_tenant_sku UNIQUE (tenant_id, sku)
);

CREATE INDEX idx_prod_tenant_category ON products(tenant_id, category);
CREATE INDEX idx_prod_tenant_active ON products(tenant_id, active);
