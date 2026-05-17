CREATE TABLE warehouses (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    code VARCHAR(20) NOT NULL,
    name VARCHAR(200) NOT NULL,
    address VARCHAR(500),
    type VARCHAR(20) NOT NULL DEFAULT 'MAIN',
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT uq_wh_tenant_code UNIQUE (tenant_id, code)
);
