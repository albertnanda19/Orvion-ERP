-- Seed warehouses
INSERT INTO warehouses (id, tenant_id, code, name, address, type, active, created_by)
VALUES
    (gen_random_uuid(), 'ORVION_DEFAULT', 'WH-MAIN', 'Main Warehouse', 'Jl. Industri Raya No. 1, Jakarta', 'MAIN', true, 'SYSTEM'),
    (gen_random_uuid(), 'ORVION_DEFAULT', 'WH-TRANSIT', 'Transit Warehouse', 'Jl. Transit No. 2, Jakarta', 'TRANSIT', true, 'SYSTEM');

-- Seed supplier
INSERT INTO suppliers (id, tenant_id, code, name, contact_email, contact_phone, address, payment_terms, performance_score, active, created_by)
VALUES
    (gen_random_uuid(), 'ORVION_DEFAULT', 'SUPP-001', 'PT Bahan Baku Utama', 'sales@bahanbaku.co.id', '021-12345678', 'Jl. Supplier No. 1, Jakarta', 'NET30', 4.50, true, 'SYSTEM'),
    (gen_random_uuid(), 'ORVION_DEFAULT', 'SUPP-002', 'CV Material Sejahtera', 'order@materialsejahtera.com', '021-87654321', 'Jl. Material No. 2, Bandung', 'NET45', 4.00, true, 'SYSTEM');

-- Seed products
INSERT INTO products (id, tenant_id, sku, name, description, category, unit, current_stock, reserved_stock, reorder_point, reorder_quantity, preferred_supplier_id, warehouse_id, standard_cost, cost_currency, costing_method, active, created_by)
SELECT
    p.id, 'ORVION_DEFAULT', p.sku, p.name, p."desc", p.category, p.unit, 1000.0000, 0.0000, p.reorder_point, p.reorder_qty, s.id::text, w.id::text, p.cost, 'IDR', p.method, true, 'SYSTEM'
FROM (
    VALUES
        (gen_random_uuid(), 'RAW-001', 'Raw Material Alpha', 'Primary raw material for production', 'RAW_MATERIALS', 'KG', 100.0000, 500.0000, 25000.0000, 'FIFO'),
        (gen_random_uuid(), 'RAW-002', 'Raw Material Beta', 'Secondary raw material', 'RAW_MATERIALS', 'KG', 50.0000, 200.0000, 15000.0000, 'AVERAGE_COST'),
        (gen_random_uuid(), 'PKG-001', 'Standard Packaging Box', 'Standard corrugated box for finished goods', 'PACKAGING', 'PCS', 500.0000, 2000.0000, 5000.0000, 'LIFO'),
        (gen_random_uuid(), 'FIN-001', 'Finished Product A', 'Main finished good for sale', 'FINISHED_GOODS', 'PCS', 200.0000, 500.0000, 100000.0000, 'AVERAGE_COST'),
        (gen_random_uuid(), 'MRO-001', 'Lubricant Oil', 'Industrial lubricant for machinery', 'MRO', 'LTR', 50.0000, 100.0000, 75000.0000, 'FIFO')
) AS p(id, sku, name, "desc", category, unit, reorder_point, reorder_qty, cost, method)
CROSS JOIN LATERAL (SELECT id FROM suppliers WHERE tenant_id = 'ORVION_DEFAULT' AND code = 'SUPP-001' LIMIT 1) s
CROSS JOIN LATERAL (SELECT id FROM warehouses WHERE tenant_id = 'ORVION_DEFAULT' AND code = 'WH-MAIN' LIMIT 1) w;
