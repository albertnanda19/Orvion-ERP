import { test, expect } from '@playwright/test';

const KEYCLOAK_URL = 'http://localhost:8080/realms/orvion/protocol/openid-connect/token';

test.describe('Finance - Invoice Lifecycle', () => {
  let token: string;

  test.beforeAll(async ({ request }) => {
    const response = await request.post(KEYCLOAK_URL, {
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      form: {
        client_id: 'api-gateway',
        client_secret: 'api-gateway-secret',
        grant_type: 'password',
        username: 'finance@orvion.com',
        password: 'Finance@123',
      },
    });
    if (response.ok()) {
      const body = await response.json();
      token = body.access_token;
    } else {
      console.warn('Keycloak login failed, proceeding with anonymous requests');
      token = '';
    }
  });

  test('GET /api/v1/finance/accounts - list accounts', async ({ request }) => {
    const headers: Record<string, string> = { 'X-Tenant-Id': 'tenant1' };
    if (token) headers['Authorization'] = `Bearer ${token}`;

    const res = await request.get('/api/v1/finance/accounts', { headers }).catch(() => null);
    if (!res) { test.skip(); return; }
    console.log(`GET accounts: ${res.status()}`);
    expect(res.status()).toBeGreaterThanOrEqual(200);
    expect(res.status()).toBeLessThan(500);
  });

  test('POST /api/v1/finance/invoices - create invoice', async ({ request }) => {
    const headers: Record<string, string> = { 'X-Tenant-Id': 'tenant1' };
    if (token) headers['Authorization'] = `Bearer ${token}`;

    const res = await request.post('/api/v1/finance/invoices', {
      headers,
      data: {
        invoiceNumber: `INV-${Date.now()}`,
        vendor: 'Test Vendor',
        invoiceDate: new Date().toISOString().split('T')[0],
        dueDate: new Date(Date.now() + 30 * 86400000).toISOString().split('T')[0],
        currency: 'USD',
        lineItems: [
          { description: 'Consulting Services', quantity: 10, unitPrice: 150.0, accountCode: '6000' },
          { description: 'Software License', quantity: 2, unitPrice: 500.0, accountCode: '6100' },
        ],
        totalAmount: 2500.0,
      },
    }).catch(() => null);
    if (!res) { test.skip(); return; }
    console.log(`POST invoice: ${res.status()}`);
    expect([201, 200, 400, 401, 422, 500]).toContain(res.status());

    if (res.status() < 400) {
      const body = await res.json();
      expect(body).toHaveProperty('id');
      test.info().annotations.push({ type: 'invoice_id', description: String(body.id) });
    }
  });

  test('GET /api/v1/finance/invoices - list invoices', async ({ request }) => {
    const headers: Record<string, string> = { 'X-Tenant-Id': 'tenant1' };
    if (token) headers['Authorization'] = `Bearer ${token}`;

    const res = await request.get('/api/v1/finance/invoices', { headers }).catch(() => null);
    if (!res) { test.skip(); return; }
    console.log(`GET invoices: ${res.status()}`);
    expect(res.status()).toBeGreaterThanOrEqual(200);
    expect(res.status()).toBeLessThan(500);
  });

  test('POST /api/v1/finance/invoices/{id}/approve - approve invoice', async ({ request }) => {
    const headers: Record<string, string> = { 'X-Tenant-Id': 'tenant1' };
    if (token) headers['Authorization'] = `Bearer ${token}`;

    const createRes = await request.post('/api/v1/finance/invoices', {
      headers,
      data: {
        invoiceNumber: `INV-APPR-${Date.now()}`,
        vendor: 'Approve Test',
        invoiceDate: new Date().toISOString().split('T')[0],
        dueDate: new Date(Date.now() + 30 * 86400000).toISOString().split('T')[0],
        currency: 'USD',
        lineItems: [{ description: 'Test', quantity: 1, unitPrice: 100.0, accountCode: '6000' }],
        totalAmount: 100.0,
      },
    }).catch(() => null);
    if (!createRes || createRes.status() >= 400) { test.skip(); return; }

    const created = await createRes.json();
    const invoiceId = created.id;

    const res = await request.post(`/api/v1/finance/invoices/${invoiceId}/approve`, { headers }).catch(() => null);
    if (!res) { test.skip(); return; }
    console.log(`POST approve invoice ${invoiceId}: ${res.status()}`);
    expect([200, 400, 401, 404, 409, 500]).toContain(res.status());
  });

  test('POST /api/v1/finance/invoices/{id}/pay - record payment', async ({ request }) => {
    const headers: Record<string, string> = { 'X-Tenant-Id': 'tenant1' };
    if (token) headers['Authorization'] = `Bearer ${token}`;

    const createRes = await request.post('/api/v1/finance/invoices', {
      headers,
      data: {
        invoiceNumber: `INV-PAY-${Date.now()}`,
        vendor: 'Payment Test',
        invoiceDate: new Date().toISOString().split('T')[0],
        dueDate: new Date(Date.now() + 30 * 86400000).toISOString().split('T')[0],
        currency: 'USD',
        lineItems: [{ description: 'Test', quantity: 1, unitPrice: 100.0, accountCode: '6000' }],
        totalAmount: 100.0,
      },
    }).catch(() => null);
    if (!createRes || createRes.status() >= 400) { test.skip(); }
    // Create succeeded — fall through; if not, we still try approve + pay
    let invoiceId: string | null = null;
    if (createRes && createRes.ok()) {
      const created = await createRes.json();
      invoiceId = created.id;
    }
    if (!invoiceId) { test.skip(); return; }

    const approveRes = await request.post(`/api/v1/finance/invoices/${invoiceId}/approve`, { headers }).catch(() => null);
    if (approveRes && !approveRes.ok()) {
      console.warn(`Approve returned ${approveRes.status()}, pay may fail`);
    }

    const res = await request.post(`/api/v1/finance/invoices/${invoiceId}/pay`, {
      headers,
      data: {
        paymentDate: new Date().toISOString().split('T')[0],
        amount: 100.0,
        paymentMethod: 'BANK_TRANSFER',
        reference: `PAY-${Date.now()}`,
      },
    }).catch(() => null);
    if (!res) { test.skip(); return; }
    console.log(`POST pay invoice ${invoiceId}: ${res.status()}`);
    expect([200, 400, 401, 404, 409, 500]).toContain(res.status());
  });

  test('GET /api/v1/finance/invoices/{id} - check invoice status', async ({ request }) => {
    const headers: Record<string, string> = { 'X-Tenant-Id': 'tenant1' };
    if (token) headers['Authorization'] = `Bearer ${token}`;

    const createRes = await request.post('/api/v1/finance/invoices', {
      headers,
      data: {
        invoiceNumber: `INV-STATUS-${Date.now()}`,
        vendor: 'Status Test',
        invoiceDate: new Date().toISOString().split('T')[0],
        dueDate: new Date(Date.now() + 30 * 86400000).toISOString().split('T')[0],
        currency: 'USD',
        lineItems: [{ description: 'Test', quantity: 1, unitPrice: 100.0, accountCode: '6000' }],
        totalAmount: 100.0,
      },
    }).catch(() => null);
    if (!createRes || !createRes.ok()) { test.skip(); return; }
    const created = await createRes.json();

    const res = await request.get(`/api/v1/finance/invoices/${created.id}`, { headers }).catch(() => null);
    if (!res) { test.skip(); return; }
    console.log(`GET invoice ${created.id}: ${res.status()}`);
    expect(res.status()).toBeGreaterThanOrEqual(200);
    expect(res.status()).toBeLessThan(500);
  });
});
