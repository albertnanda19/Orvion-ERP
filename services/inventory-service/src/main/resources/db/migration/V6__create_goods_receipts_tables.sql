CREATE TABLE goods_receipts (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    receipt_number VARCHAR(50) NOT NULL,
    purchase_order_id UUID NOT NULL,
    warehouse_id UUID NOT NULL,
    received_by VARCHAR(50),
    received_at TIMESTAMPTZ,
    notes VARCHAR(1000),
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT uq_gr_tenant_number UNIQUE (tenant_id, receipt_number)
);

CREATE TABLE goods_receipt_lines (
    id UUID PRIMARY KEY,
    goods_receipt_id UUID NOT NULL REFERENCES goods_receipts(id),
    purchase_order_line_id UUID NOT NULL,
    product_id UUID NOT NULL,
    product_name VARCHAR(200),
    quantity NUMERIC(18,4) NOT NULL,
    accepted_quantity NUMERIC(18,4) NOT NULL,
    rejected_quantity NUMERIC(18,4),
    unit_cost NUMERIC(18,4),
    line_cost_currency VARCHAR(3)
);

CREATE INDEX idx_gr_tenant_po ON goods_receipts(tenant_id, purchase_order_id);
CREATE INDEX idx_grl_gr ON goods_receipt_lines(goods_receipt_id);
