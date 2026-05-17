CREATE TABLE budget_plans (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id VARCHAR(50) NOT NULL,
    department VARCHAR(100),
    account_code VARCHAR(20) NOT NULL,
    account_name VARCHAR(200),
    budgeted_amount NUMERIC(19,4) NOT NULL,
    currency VARCHAR(3),
    actual_amount NUMERIC(19,4),
    actual_currency VARCHAR(3),
    year INT NOT NULL,
    month INT NOT NULL,
    notes TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT uq_budget_tenant_dept_account_period UNIQUE (tenant_id, department, account_code, year, month)
);

CREATE INDEX idx_budget_tenant_department ON budget_plans(tenant_id, department);
CREATE INDEX idx_budget_tenant_period ON budget_plans(tenant_id, year, month);
