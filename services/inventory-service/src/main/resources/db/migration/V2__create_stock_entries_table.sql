CREATE TABLE stock_entries (
    id UUID PRIMARY KEY,
    product_id UUID NOT NULL REFERENCES products(id),
    quantity NUMERIC(18,4) NOT NULL,
    remaining_quantity NUMERIC(18,4) NOT NULL,
    unit_cost NUMERIC(18,4),
    cost_currency VARCHAR(3),
    received_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_se_product ON stock_entries(product_id);
CREATE INDEX idx_se_received_at ON stock_entries(received_at);
