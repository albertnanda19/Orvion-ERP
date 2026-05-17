CREATE TABLE notification_logs (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    recipient_id VARCHAR(100),
    recipient_email VARCHAR(255),
    channel VARCHAR(20) NOT NULL,
    subject VARCHAR(500),
    body TEXT,
    status VARCHAR(20) NOT NULL,
    event_id VARCHAR(100),
    event_type VARCHAR(100),
    error_message TEXT,
    sent_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE INDEX idx_nl_tenant_recipient ON notification_logs(tenant_id, recipient_id);
CREATE INDEX idx_nl_tenant_event ON notification_logs(tenant_id, event_id);
CREATE INDEX idx_nl_status ON notification_logs(status);
CREATE INDEX idx_nl_sent_at ON notification_logs(sent_at);
