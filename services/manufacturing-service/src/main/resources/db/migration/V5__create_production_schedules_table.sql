CREATE TABLE production_schedules (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    date DATE NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT uq_ps_tenant_date UNIQUE (tenant_id, date)
);

CREATE TABLE schedule_work_orders (
    schedule_id UUID NOT NULL REFERENCES production_schedules(id) ON DELETE CASCADE,
    work_order_id VARCHAR(50) NOT NULL
);

CREATE TABLE schedule_machine_allocations (
    schedule_id UUID NOT NULL REFERENCES production_schedules(id) ON DELETE CASCADE,
    machine_id VARCHAR(50) NOT NULL,
    work_order_id VARCHAR(50) NOT NULL,
    PRIMARY KEY (schedule_id, machine_id)
);

CREATE INDEX idx_ps_tenant_date ON production_schedules(tenant_id, date);
