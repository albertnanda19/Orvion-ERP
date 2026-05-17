CREATE TABLE work_orders (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    order_number VARCHAR(20) NOT NULL,
    product_id VARCHAR(50) NOT NULL,
    planned_quantity NUMERIC(18,4) NOT NULL,
    actual_quantity NUMERIC(18,4),
    bom_id VARCHAR(50),
    status VARCHAR(20) NOT NULL DEFAULT 'PLANNED',
    planned_start TIMESTAMPTZ NOT NULL,
    planned_end TIMESTAMPTZ NOT NULL,
    actual_start TIMESTAMPTZ,
    actual_end TIMESTAMPTZ,
    warehouse_id VARCHAR(50),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT uq_wo_tenant_order_number UNIQUE (tenant_id, order_number)
);

CREATE INDEX idx_wo_tenant_status ON work_orders(tenant_id, status);
CREATE INDEX idx_wo_tenant_product ON work_orders(tenant_id, product_id);
