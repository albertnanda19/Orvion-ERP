CREATE TABLE bill_of_materials (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    product_id VARCHAR(50) NOT NULL,
    version INTEGER NOT NULL,
    effective_date TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT uq_bom_tenant_product_version UNIQUE (tenant_id, product_id, version)
);

CREATE TABLE bom_components (
    id UUID PRIMARY KEY,
    bom_id UUID NOT NULL REFERENCES bill_of_materials(id) ON DELETE CASCADE,
    component_product_id VARCHAR(50) NOT NULL,
    quantity NUMERIC(18,4) NOT NULL,
    unit VARCHAR(20) NOT NULL,
    waste_percentage NUMERIC(5,2) DEFAULT 0
);

CREATE INDEX idx_bom_tenant_product ON bill_of_materials(tenant_id, product_id);
CREATE INDEX idx_bom_tenant_active ON bill_of_materials(tenant_id, active);
CREATE INDEX idx_bc_bom_id ON bom_components(bom_id);
