-- Seed standard PSAK-aligned chart of accounts for tenant1
INSERT INTO accounts (id, tenant_id, code, name, type, description, active, parent_account_id, level,
                      current_balance_amount, current_balance_currency,
                      opening_balance_amount, opening_balance_currency)
VALUES
    -- ASSETS (1xxx)
    (gen_random_uuid(), 'tenant1', '1000', 'ASET LANCAR', 'ASSET', 'Current Assets', true, NULL, 1, 0, 'IDR', 0, 'IDR'),
    (gen_random_uuid(), 'tenant1', '1100', 'Kas', 'ASSET', 'Cash on hand and in banks', true, (SELECT id FROM accounts a2 WHERE a2.tenant_id='tenant1' AND a2.code='1000'), 2, 0, 'IDR', 0, 'IDR'),
    (gen_random_uuid(), 'tenant1', '1200', 'Piutang Usaha', 'ASSET', 'Accounts Receivable', true, (SELECT id FROM accounts a2 WHERE a2.tenant_id='tenant1' AND a2.code='1000'), 2, 0, 'IDR', 0, 'IDR'),
    (gen_random_uuid(), 'tenant1', '1300', 'Persediaan', 'ASSET', 'Inventory', true, (SELECT id FROM accounts a2 WHERE a2.tenant_id='tenant1' AND a2.code='1000'), 2, 0, 'IDR', 0, 'IDR'),

    (gen_random_uuid(), 'tenant1', '2000', 'ASET TETAP', 'ASSET', 'Fixed Assets', true, NULL, 1, 0, 'IDR', 0, 'IDR'),
    (gen_random_uuid(), 'tenant1', '2100', 'Tanah', 'ASSET', 'Land', true, (SELECT id FROM accounts a2 WHERE a2.tenant_id='tenant1' AND a2.code='2000'), 2, 0, 'IDR', 0, 'IDR'),
    (gen_random_uuid(), 'tenant1', '2200', 'Bangunan', 'ASSET', 'Buildings', true, (SELECT id FROM accounts a2 WHERE a2.tenant_id='tenant1' AND a2.code='2000'), 2, 0, 'IDR', 0, 'IDR'),
    (gen_random_uuid(), 'tenant1', '2300', 'Akumulasi Penyusutan', 'ASSET', 'Accumulated Depreciation', true, (SELECT id FROM accounts a2 WHERE a2.tenant_id='tenant1' AND a2.code='2000'), 2, 0, 'IDR', 0, 'IDR'),

    -- LIABILITIES (2xxx)
    (gen_random_uuid(), 'tenant1', '3000', 'KEWAJIBAN', 'LIABILITY', 'Liabilities', true, NULL, 1, 0, 'IDR', 0, 'IDR'),
    (gen_random_uuid(), 'tenant1', '3100', 'Utang Usaha', 'LIABILITY', 'Accounts Payable', true, (SELECT id FROM accounts a2 WHERE a2.tenant_id='tenant1' AND a2.code='3000'), 2, 0, 'IDR', 0, 'IDR'),
    (gen_random_uuid(), 'tenant1', '3200', 'Utang Pajak', 'LIABILITY', 'Tax Payable', true, (SELECT id FROM accounts a2 WHERE a2.tenant_id='tenant1' AND a2.code='3000'), 2, 0, 'IDR', 0, 'IDR'),
    (gen_random_uuid(), 'tenant1', '3300', 'Utang Bank', 'LIABILITY', 'Bank Loan Payable', true, (SELECT id FROM accounts a2 WHERE a2.tenant_id='tenant1' AND a2.code='3000'), 2, 0, 'IDR', 0, 'IDR'),

    -- EQUITY (3xxx)
    (gen_random_uuid(), 'tenant1', '4000', 'EKUITAS', 'EQUITY', 'Equity', true, NULL, 1, 0, 'IDR', 0, 'IDR'),
    (gen_random_uuid(), 'tenant1', '4100', 'Modal Disetor', 'EQUITY', 'Paid-in Capital', true, (SELECT id FROM accounts a2 WHERE a2.tenant_id='tenant1' AND a2.code='4000'), 2, 0, 'IDR', 0, 'IDR'),
    (gen_random_uuid(), 'tenant1', '4200', 'Laba Ditahan', 'EQUITY', 'Retained Earnings', true, (SELECT id FROM accounts a2 WHERE a2.tenant_id='tenant1' AND a2.code='4000'), 2, 0, 'IDR', 0, 'IDR'),

    -- REVENUE (4xxx)
    (gen_random_uuid(), 'tenant1', '5000', 'PENDAPATAN', 'REVENUE', 'Revenue', true, NULL, 1, 0, 'IDR', 0, 'IDR'),
    (gen_random_uuid(), 'tenant1', '5100', 'Pendapatan Penjualan', 'REVENUE', 'Sales Revenue', true, (SELECT id FROM accounts a2 WHERE a2.tenant_id='tenant1' AND a2.code='5000'), 2, 0, 'IDR', 0, 'IDR'),
    (gen_random_uuid(), 'tenant1', '5200', 'Pendapatan Jasa', 'REVENUE', 'Service Revenue', true, (SELECT id FROM accounts a2 WHERE a2.tenant_id='tenant1' AND a2.code='5000'), 2, 0, 'IDR', 0, 'IDR'),
    (gen_random_uuid(), 'tenant1', '5300', 'Pendapatan Lain-lain', 'REVENUE', 'Other Income', true, (SELECT id FROM accounts a2 WHERE a2.tenant_id='tenant1' AND a2.code='5000'), 2, 0, 'IDR', 0, 'IDR'),

    -- EXPENSES (5xxx)
    (gen_random_uuid(), 'tenant1', '6000', 'BEBAN', 'EXPENSE', 'Expenses', true, NULL, 1, 0, 'IDR', 0, 'IDR'),
    (gen_random_uuid(), 'tenant1', '6100', 'Beban Gaji', 'EXPENSE', 'Salary Expense', true, (SELECT id FROM accounts a2 WHERE a2.tenant_id='tenant1' AND a2.code='6000'), 2, 0, 'IDR', 0, 'IDR'),
    (gen_random_uuid(), 'tenant1', '6200', 'Beban Sewa', 'EXPENSE', 'Rent Expense', true, (SELECT id FROM accounts a2 WHERE a2.tenant_id='tenant1' AND a2.code='6000'), 2, 0, 'IDR', 0, 'IDR'),
    (gen_random_uuid(), 'tenant1', '6300', 'Beban Utilitas', 'EXPENSE', 'Utilities Expense', true, (SELECT id FROM accounts a2 WHERE a2.tenant_id='tenant1' AND a2.code='6000'), 2, 0, 'IDR', 0, 'IDR'),
    (gen_random_uuid(), 'tenant1', '6400', 'Beban Penyusutan', 'EXPENSE', 'Depreciation Expense', true, (SELECT id FROM accounts a2 WHERE a2.tenant_id='tenant1' AND a2.code='6000'), 2, 0, 'IDR', 0, 'IDR'),
    (gen_random_uuid(), 'tenant1', '6500', 'Beban Pajak', 'EXPENSE', 'Tax Expense', true, (SELECT id FROM accounts a2 WHERE a2.tenant_id='tenant1' AND a2.code='6000'), 2, 0, 'IDR', 0, 'IDR'),
    (gen_random_uuid(), 'tenant1', '6600', 'Beban Operasional Lain', 'EXPENSE', 'Other Operating Expense', true, (SELECT id FROM accounts a2 WHERE a2.tenant_id='tenant1' AND a2.code='6000'), 2, 0, 'IDR', 0, 'IDR');
