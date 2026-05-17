CREATE TABLE report_definitions (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    name VARCHAR(200) NOT NULL,
    description VARCHAR(1000),
    report_type VARCHAR(30) NOT NULL,
    query_config TEXT,
    schedule_config VARCHAR(100),
    output_format VARCHAR(10),
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

CREATE INDEX idx_repdef_tenant_active ON report_definitions(tenant_id, active);

CREATE TABLE report_executions (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    report_definition_id UUID NOT NULL,
    triggered_by VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    parameters TEXT,
    result_file_size BIGINT,
    result_file_path VARCHAR(500),
    execution_duration_ms BIGINT,
    error_message TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

CREATE INDEX idx_repexec_tenant_status ON report_executions(tenant_id, status);
CREATE INDEX idx_repexec_def_id ON report_executions(report_definition_id);
