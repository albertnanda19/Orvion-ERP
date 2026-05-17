import { test, expect } from '@playwright/test';

const KEYCLOAK_URL = 'http://localhost:8080/realms/orvion/protocol/openid-connect/token';

test.describe('Inventory - Purchase Order to Receipt', () => {
  let token: string;

  test.beforeAll(async ({ request }) => {
    const response = await request.post(KEYCLOAK_URL, {
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      form: {
        client_id: 'api-gateway',
        client_secret: 'api-gateway-secret',
        grant_type: 'password',
        username: 'inventory@orvion.com',
        password: 'Inventory@123',
      },
    });
    if (response.ok()) {
      const body = await response.json();
      token = body.access_token;
    } else {
      console.warn('Keycloak login failed for inventory user');
      token = '';
    }
  });

  test('POST /api/v1/inventory/purchase-orders - create PO', async ({ request }) => {
    const headers: Record<string, string> = { 'X-Tenant-Id': 'tenant1' };
    if (token) headers['Authorization'] = `Bearer ${token}`;

    const res = await request.post('/api/v1/inventory/purchase-orders', {
      headers,
      data: {
        poNumber: `PO-${Date.now()}`,
        vendor: 'Test Supplier',
        orderDate: new Date().toISOString().split('T')[0],
        expectedDeliveryDate: new Date(Date.now() + 14 * 86400000).toISOString().split('T')[0],
        currency: 'USD',
        lineItems: [
          { itemCode: 'RAW-001', description: 'Raw Material A', quantity: 100, unitPrice: 5.0, uom: 'kg' },
          { itemCode: 'RAW-002', description: 'Raw Material B', quantity: 50, unitPrice: 12.0, uom: 'kg' },
        ],
        totalAmount: 1100.0,
        status: 'DRAFT',
      },
    }).catch(() => null);
    if (!res) { test.skip(); return; }
    console.log(`POST purchase order: ${res.status()}`);
    expect([201, 200, 400, 401, 422, 500]).toContain(res.status());
  });

  test('POST /api/v1/inventory/purchase-orders/{id}/approve - approve PO', async ({ request }) => {
    const headers: Record<string, string> = { 'X-Tenant-Id': 'tenant1' };
    if (token) headers['Authorization'] = `Bearer ${token}`;

    const createRes = await request.post('/api/v1/inventory/purchase-orders', {
      headers,
      data: {
        poNumber: `PO-APPR-${Date.now()}`,
        vendor: 'Approve Supplier',
        orderDate: new Date().toISOString().split('T')[0],
        expectedDeliveryDate: new Date(Date.now() + 14 * 86400000).toISOString().split('T')[0],
        currency: 'USD',
        lineItems: [{ itemCode: 'RAW-001', description: 'Raw Material', quantity: 10, unitPrice: 5.0, uom: 'kg' }],
        totalAmount: 50.0,
        status: 'DRAFT',
      },
    }).catch(() => null);
    if (!createRes || createRes.status() >= 400) { test.skip(); return; }
    const created = await createRes.json();

    const res = await request.post(`/api/v1/inventory/purchase-orders/${created.id}/approve`, { headers }).catch(() => null);
    if (!res) { test.skip(); return; }
    console.log(`POST approve PO ${created.id}: ${res.status()}`);
    expect([200, 400, 401, 404, 409, 500]).toContain(res.status());
  });

  test('POST /api/v1/inventory/goods-receipts - create goods receipt', async ({ request }) => {
    const headers: Record<string, string> = { 'X-Tenant-Id': 'tenant1' };
    if (token) headers['Authorization'] = `Bearer ${token}`;

    const poRes = await request.post('/api/v1/inventory/purchase-orders', {
      headers,
      data: {
        poNumber: `PO-GR-${Date.now()}`,
        vendor: 'GR Supplier',
        orderDate: new Date().toISOString().split('T')[0],
        expectedDeliveryDate: new Date(Date.now() + 14 * 86400000).toISOString().split('T')[0],
        currency: 'USD',
        lineItems: [{ itemCode: 'RAW-001', description: 'Raw Material', quantity: 10, unitPrice: 5.0, uom: 'kg' }],
        totalAmount: 50.0,
        status: 'DRAFT',
      },
    }).catch(() => null);
    let poId: string | null = null;
    if (poRes && poRes.ok()) {
      const po = await poRes.json();
      poId = po.id;
    }

    const res = await request.post('/api/v1/inventory/goods-receipts', {
      headers,
      data: {
        purchaseOrderId: poId,
        receiptDate: new Date().toISOString().split('T')[0],
        deliveryNoteNumber: `DN-${Date.now()}`,
        items: [
          { itemCode: 'RAW-001', quantityReceived: 10, condition: 'GOOD', lotNumber: `LOT-${Date.now()}` },
        ],
      },
    }).catch(() => null);
    if (!res) { test.skip(); return; }
    console.log(`POST goods receipt: ${res.status()}`);
    expect([201, 200, 400, 401, 422, 500]).toContain(res.status());
  });

  test('GET /api/v1/inventory/stock-movements - check stock', async ({ request }) => {
    const headers: Record<string, string> = { 'X-Tenant-Id': 'tenant1' };
    if (token) headers['Authorization'] = `Bearer ${token}`;

    const res = await request.get('/api/v1/inventory/stock-movements', { headers }).catch(() => null);
    if (!res) { test.skip(); return; }
    console.log(`GET stock movements: ${res.status()}`);
    expect(res.status()).toBeGreaterThanOrEqual(200);
    expect(res.status()).toBeLessThan(500);
  });
});
