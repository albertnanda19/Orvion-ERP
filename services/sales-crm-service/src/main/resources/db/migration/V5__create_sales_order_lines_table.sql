CREATE TABLE sales_order_lines (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL REFERENCES sales_orders(id),
    product_id VARCHAR(100) NOT NULL,
    product_name VARCHAR(255),
    sku VARCHAR(100),
    quantity DECIMAL(19,4) NOT NULL,
    reserved_quantity DECIMAL(19,4) DEFAULT 0,
    unit_price_amount DECIMAL(19,4),
    unit_price_currency VARCHAR(3) DEFAULT 'IDR',
    line_total_amount DECIMAL(19,4),
    line_total_currency VARCHAR(3) DEFAULT 'IDR',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE INDEX idx_sol_order ON sales_order_lines(order_id);
