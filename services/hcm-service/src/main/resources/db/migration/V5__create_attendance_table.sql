CREATE TABLE attendance (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    employee_id UUID NOT NULL REFERENCES employees(id),
    date DATE NOT NULL,
    clock_in TIME,
    clock_out TIME,
    working_hours NUMERIC(21,0),
    is_late BOOLEAN DEFAULT false,
    is_overtime BOOLEAN DEFAULT false,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT uq_att_employee_date UNIQUE (employee_id, date)
);
