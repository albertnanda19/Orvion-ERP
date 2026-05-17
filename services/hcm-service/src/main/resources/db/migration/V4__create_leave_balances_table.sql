CREATE TABLE leave_balances (
    id UUID PRIMARY KEY,
    employee_id UUID NOT NULL REFERENCES employees(id),
    year INT NOT NULL,
    leave_type VARCHAR(20) NOT NULL,
    total_days DECIMAL(5,1) NOT NULL DEFAULT 12.0,
    used_days DECIMAL(5,1) NOT NULL DEFAULT 0.0,
    CONSTRAINT uq_lb_employee_year_type UNIQUE (employee_id, year, leave_type)
);
