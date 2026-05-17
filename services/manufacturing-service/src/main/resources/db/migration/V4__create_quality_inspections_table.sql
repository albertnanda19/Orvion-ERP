CREATE TABLE quality_inspections (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    work_order_id VARCHAR(50) NOT NULL,
    inspected_by VARCHAR(100) NOT NULL,
    inspection_date TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    passed_quantity NUMERIC(18,4) NOT NULL,
    failed_quantity NUMERIC(18,4) NOT NULL,
    defect_reasons TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE INDEX idx_qi_tenant_work_order ON quality_inspections(tenant_id, work_order_id);
CREATE INDEX idx_qi_tenant_status ON quality_inspections(tenant_id, status);
