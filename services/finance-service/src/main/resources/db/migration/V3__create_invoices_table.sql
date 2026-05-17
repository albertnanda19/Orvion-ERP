CREATE TABLE invoices (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id VARCHAR(50) NOT NULL,
    invoice_number VARCHAR(50) NOT NULL,
    type VARCHAR(25) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    counterparty_id VARCHAR(50),
    counterparty_name VARCHAR(200),
    issue_date TIMESTAMPTZ,
    due_date TIMESTAMPTZ NOT NULL,
    subtotal_amount NUMERIC(19,4),
    subtotal_currency VARCHAR(3),
    tax_amount NUMERIC(19,4),
    tax_currency VARCHAR(3),
    total_amount NUMERIC(19,4) NOT NULL,
    total_currency VARCHAR(3) NOT NULL,
    paid_amount NUMERIC(19,4),
    paid_currency VARCHAR(3),
    outstanding_amount NUMERIC(19,4),
    outstanding_currency VARCHAR(3),
    currency VARCHAR(3),
    payment_ids TEXT,
    notes TEXT,
    approved_by VARCHAR(100),
    approved_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT uq_invoice_number UNIQUE (invoice_number)
);

CREATE INDEX idx_inv_tenant_status ON invoices(tenant_id, status);
CREATE INDEX idx_inv_tenant_counterparty ON invoices(tenant_id, counterparty_id);
CREATE INDEX idx_inv_due_date ON invoices(due_date);

CREATE TABLE invoice_line_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    invoice_id UUID NOT NULL REFERENCES invoices(id) ON DELETE CASCADE,
    description VARCHAR(500) NOT NULL,
    quantity NUMERIC(19,4) NOT NULL,
    unit_price NUMERIC(19,4) NOT NULL,
    tax_rate NUMERIC(5,2),
    subtotal NUMERIC(19,4),
    currency VARCHAR(3),
    tax_amount NUMERIC(19,4),
    tax_currency VARCHAR(3),
    line_number INT,
    CONSTRAINT chk_quantity_positive CHECK (quantity > 0),
    CONSTRAINT chk_unit_price_positive CHECK (unit_price > 0)
);

CREATE INDEX idx_ili_invoice ON invoice_line_items(invoice_id);
