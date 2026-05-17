CREATE TABLE sales_orders (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    order_number VARCHAR(20) UNIQUE NOT NULL,
    customer_id VARCHAR(100) NOT NULL,
    assigned_to VARCHAR(100),
    status VARCHAR(50) NOT NULL,
    total_amount DECIMAL(19,4),
    total_currency VARCHAR(3) DEFAULT 'IDR',
    order_date TIMESTAMP WITH TIME ZONE,
    expected_delivery TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE INDEX idx_so_tenant_status ON sales_orders(tenant_id, status);
CREATE INDEX idx_so_tenant_customer ON sales_orders(tenant_id, customer_id);
