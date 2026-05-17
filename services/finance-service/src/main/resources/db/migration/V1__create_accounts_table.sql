CREATE TABLE accounts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id VARCHAR(50) NOT NULL,
    code VARCHAR(20) NOT NULL,
    name VARCHAR(200) NOT NULL,
    type VARCHAR(20) NOT NULL,
    description TEXT,
    active BOOLEAN NOT NULL DEFAULT true,
    parent_account_id UUID REFERENCES accounts(id),
    level INT NOT NULL DEFAULT 1,
    current_balance_amount NUMERIC(19,4) NOT NULL DEFAULT 0,
    current_balance_currency VARCHAR(3) NOT NULL DEFAULT 'IDR',
    opening_balance_amount NUMERIC(19,4) NOT NULL DEFAULT 0,
    opening_balance_currency VARCHAR(3) NOT NULL DEFAULT 'IDR',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT uq_tenant_account_code UNIQUE (tenant_id, code)
);

CREATE INDEX idx_accounts_tenant_type ON accounts(tenant_id, type);
CREATE INDEX idx_accounts_tenant_code ON accounts(tenant_id, code);
