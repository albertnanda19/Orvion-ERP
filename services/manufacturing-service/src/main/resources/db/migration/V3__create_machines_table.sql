CREATE TABLE machines (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    machine_id VARCHAR(50) NOT NULL,
    name VARCHAR(200) NOT NULL,
    type VARCHAR(100),
    status VARCHAR(20) NOT NULL DEFAULT 'IDLE',
    oee_target NUMERIC(5,2),
    last_maintenance_date TIMESTAMPTZ,
    next_maintenance_date TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT uq_mach_tenant_machine_id UNIQUE (tenant_id, machine_id)
);

CREATE INDEX idx_mach_tenant_status ON machines(tenant_id, status);
