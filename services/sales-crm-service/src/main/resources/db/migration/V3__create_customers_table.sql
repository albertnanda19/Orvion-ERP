CREATE TABLE customers (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    code VARCHAR(20) UNIQUE NOT NULL,
    name VARCHAR(200) NOT NULL,
    email VARCHAR(255),
    phone VARCHAR(50),
    address TEXT,
    credit_limit_amount DECIMAL(19,4),
    credit_limit_currency VARCHAR(3) DEFAULT 'IDR',
    outstanding_amount DECIMAL(19,4),
    outstanding_currency VARCHAR(3) DEFAULT 'IDR',
    payment_terms VARCHAR(100),
    customer_type VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE INDEX idx_cust_tenant_type ON customers(tenant_id, customer_type);
CREATE INDEX idx_cust_tenant_code ON customers(tenant_id, code);
