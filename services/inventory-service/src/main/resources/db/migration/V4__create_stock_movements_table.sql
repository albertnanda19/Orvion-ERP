CREATE TABLE stock_movements (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    product_id UUID NOT NULL,
    warehouse_id UUID NOT NULL,
    movement_type VARCHAR(20) NOT NULL,
    quantity NUMERIC(18,4) NOT NULL,
    unit_cost NUMERIC(18,4),
    cost_currency VARCHAR(3),
    total_cost NUMERIC(18,4),
    total_cost_currency VARCHAR(3),
    reference VARCHAR(100),
    source_document VARCHAR(50),
    movement_date TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    performed_by VARCHAR(50),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE INDEX idx_sm_tenant_product ON stock_movements(tenant_id, product_id);
CREATE INDEX idx_sm_tenant_ref ON stock_movements(tenant_id, reference);
CREATE INDEX idx_sm_movement_date ON stock_movements(movement_date);
