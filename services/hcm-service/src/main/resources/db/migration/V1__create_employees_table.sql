CREATE TABLE employees (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    employee_id VARCHAR(20) UNIQUE NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255),
    phone VARCHAR(50),
    national_id VARCHAR(50),
    department VARCHAR(100),
    position VARCHAR(100),
    grade VARCHAR(20),
    manager_id VARCHAR(50),
    employment_type VARCHAR(20) NOT NULL,
    employment_status VARCHAR(20) NOT NULL,
    join_date TIMESTAMP WITH TIME ZONE,
    termination_date TIMESTAMP WITH TIME ZONE,
    bank_account VARCHAR(50),
    basic_salary DECIMAL(19,4),
    allowances DECIMAL(19,4),
    salary_currency VARCHAR(3) DEFAULT 'IDR',
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE INDEX idx_emp_tenant_dept ON employees(tenant_id, department);
CREATE INDEX idx_emp_tenant_status ON employees(tenant_id, employment_status);
