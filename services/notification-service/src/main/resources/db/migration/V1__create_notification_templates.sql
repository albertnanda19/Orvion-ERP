CREATE TABLE notification_templates (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    template_code VARCHAR(100) NOT NULL,
    subject VARCHAR(500) NOT NULL,
    body TEXT NOT NULL,
    channel VARCHAR(20) NOT NULL DEFAULT 'EMAIL',
    event_type VARCHAR(100) NOT NULL,
    language VARCHAR(10) NOT NULL DEFAULT 'en',
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT uq_nt_tpl_tenant_code UNIQUE (tenant_id, template_code)
);

CREATE INDEX idx_nt_tpl_event ON notification_templates(event_type);
CREATE INDEX idx_nt_tpl_active ON notification_templates(active);
