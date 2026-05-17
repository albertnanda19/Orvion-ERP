CREATE TABLE payroll_records (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    employee_id UUID NOT NULL REFERENCES employees(id),
    period_year INT NOT NULL,
    period_month INT NOT NULL,
    basic_salary DECIMAL(19,4),
    allowances DECIMAL(19,4),
    overtime DECIMAL(19,4),
    deductions DECIMAL(19,4),
    tax_amount DECIMAL(19,4),
    net_pay DECIMAL(19,4),
    net_pay_currency VARCHAR(3) DEFAULT 'IDR',
    currency VARCHAR(3) DEFAULT 'IDR',
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE INDEX idx_pr_tenant_period ON payroll_records(tenant_id, period_year, period_month);
CREATE INDEX idx_pr_employee ON payroll_records(employee_id);
