CREATE TABLE notification_preferences (
    id UUID PRIMARY KEY,
    user_id VARCHAR(100) NOT NULL,
    tenant_id VARCHAR(50) NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    channel VARCHAR(20) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT true,
    CONSTRAINT uq_np_user_event UNIQUE (user_id, event_type)
);

CREATE INDEX idx_np_tenant ON notification_preferences(tenant_id);
