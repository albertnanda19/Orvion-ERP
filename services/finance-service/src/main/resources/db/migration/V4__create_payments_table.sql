CREATE TABLE payments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id VARCHAR(50) NOT NULL,
    invoice_id UUID NOT NULL REFERENCES invoices(id),
    amount NUMERIC(19,4) NOT NULL,
    currency VARCHAR(3),
    method VARCHAR(20) NOT NULL,
    payment_date TIMESTAMPTZ NOT NULL,
    reference VARCHAR(100),
    bank_account VARCHAR(100),
    notes TEXT,
    reconciled BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE INDEX idx_payment_invoice ON payments(invoice_id);
CREATE INDEX idx_payment_tenant_date ON payments(tenant_id, payment_date);
