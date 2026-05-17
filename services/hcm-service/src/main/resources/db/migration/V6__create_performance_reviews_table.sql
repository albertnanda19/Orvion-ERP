CREATE TABLE performance_reviews (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    employee_id UUID NOT NULL REFERENCES employees(id),
    review_period VARCHAR(20) NOT NULL,
    goals_json JSONB,
    overall_score DECIMAL(3,1),
    reviewed_by VARCHAR(100),
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE INDEX idx_pr_tenant_employee ON performance_reviews(tenant_id, employee_id);
