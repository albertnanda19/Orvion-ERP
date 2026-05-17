CREATE TABLE reporting_hcm_facts (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    period VARCHAR(10) NOT NULL,
    total_employees BIGINT,
    total_payroll DECIMAL(18,2),
    dept_counts TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

CREATE INDEX idx_hcm_fact_tenant_period ON reporting_hcm_facts(tenant_id, period);

CREATE TABLE reporting_audit_logs (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    action VARCHAR(100) NOT NULL,
    details TEXT,
    user_id VARCHAR(100),
    service_name VARCHAR(100),
    trace_id VARCHAR(50),
    timestamp TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

CREATE INDEX idx_auditlog_tenant_action ON reporting_audit_logs(tenant_id, action);
CREATE INDEX idx_auditlog_timestamp ON reporting_audit_logs(timestamp);
