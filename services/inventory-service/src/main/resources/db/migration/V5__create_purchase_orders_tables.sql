CREATE TABLE purchase_orders (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    po_number VARCHAR(50) NOT NULL,
    supplier_id VARCHAR(50) NOT NULL,
    supplier_name VARCHAR(200),
    status VARCHAR(30) NOT NULL DEFAULT 'DRAFT',
    total_amount NUMERIC(18,4),
    currency VARCHAR(3),
    order_date TIMESTAMPTZ,
    expected_delivery TIMESTAMPTZ,
    approved_by VARCHAR(50),
    approved_at TIMESTAMPTZ,
    notes VARCHAR(1000),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT uq_po_tenant_number UNIQUE (tenant_id, po_number)
);

CREATE TABLE purchase_order_lines (
    id UUID PRIMARY KEY,
    purchase_order_id UUID NOT NULL REFERENCES purchase_orders(id),
    product_id UUID NOT NULL,
    product_name VARCHAR(200),
    sku VARCHAR(50),
    quantity NUMERIC(18,4) NOT NULL,
    received_quantity NUMERIC(18,4) NOT NULL DEFAULT 0,
    unit_price NUMERIC(18,4),
    line_currency VARCHAR(3),
    total_price NUMERIC(18,4),
    total_line_currency VARCHAR(3)
);

CREATE INDEX idx_po_tenant_status ON purchase_orders(tenant_id, status);
CREATE INDEX idx_po_tenant_supplier ON purchase_orders(tenant_id, supplier_id);
CREATE INDEX idx_pol_po ON purchase_order_lines(purchase_order_id);
