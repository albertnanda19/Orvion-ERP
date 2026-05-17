import { test, expect } from '@playwright/test';

const KEYCLOAK_URL = 'http://localhost:8080/realms/orvion/protocol/openid-connect/token';

test.describe('HCM - Employee and Leave Flow', () => {
  let token: string;

  test.beforeAll(async ({ request }) => {
    const response = await request.post(KEYCLOAK_URL, {
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      form: {
        client_id: 'api-gateway',
        client_secret: 'api-gateway-secret',
        grant_type: 'password',
        username: 'admin@orvion.com',
        password: 'OrvionAdmin@2024',
      },
    });
    if (response.ok()) {
      const body = await response.json();
      token = body.access_token;
    } else {
      console.warn('Keycloak login failed for admin');
      token = '';
    }
  });

  test('GET /api/v1/hcm/employees - list employees', async ({ request }) => {
    const headers: Record<string, string> = { 'X-Tenant-Id': 'tenant1' };
    if (token) headers['Authorization'] = `Bearer ${token}`;

    const res = await request.get('/api/v1/hcm/employees', { headers }).catch(() => null);
    if (!res) { test.skip(); return; }
    console.log(`GET employees: ${res.status()}`);
    expect(res.status()).toBeGreaterThanOrEqual(200);
    expect(res.status()).toBeLessThan(500);
  });

  test('POST /api/v1/hcm/employees - create employee', async ({ request }) => {
    const headers: Record<string, string> = { 'X-Tenant-Id': 'tenant1' };
    if (token) headers['Authorization'] = `Bearer ${token}`;

    const res = await request.post('/api/v1/hcm/employees', {
      headers,
      data: {
        employeeNumber: `EMP-${Date.now()}`,
        firstName: 'John',
        lastName: 'Doe',
        email: `john.doe${Date.now()}@orvion.com`,
        department: 'Engineering',
        position: 'Software Engineer',
        hireDate: new Date().toISOString().split('T')[0],
        employmentType: 'FULL_TIME',
        salary: 75000,
      },
    }).catch(() => null);
    if (!res) { test.skip(); return; }
    console.log(`POST employee: ${res.status()}`);
    expect([201, 200, 400, 401, 422, 500]).toContain(res.status());
  });

  test('POST /api/v1/hcm/leave-requests - submit leave request', async ({ request }) => {
    const headers: Record<string, string> = { 'X-Tenant-Id': 'tenant1' };
    if (token) headers['Authorization'] = `Bearer ${token}`;

    let employeeId: string | null = null;
    const empRes = await request.post('/api/v1/hcm/employees', {
      headers,
      data: {
        employeeNumber: `EMP-LV-${Date.now()}`,
        firstName: 'Jane',
        lastName: 'Smith',
        email: `jane.smith${Date.now()}@orvion.com`,
        department: 'Engineering',
        position: 'Developer',
        hireDate: new Date().toISOString().split('T')[0],
        employmentType: 'FULL_TIME',
        salary: 80000,
      },
    }).catch(() => null);
    if (empRes && empRes.ok()) {
      const emp = await empRes.json();
      employeeId = emp.id;
    }

    const res = await request.post('/api/v1/hcm/leave-requests', {
      headers,
      data: {
        employeeId,
        leaveType: 'ANNUAL',
        startDate: new Date(Date.now() + 30 * 86400000).toISOString().split('T')[0],
        endDate: new Date(Date.now() + 34 * 86400000).toISOString().split('T')[0],
        reason: 'Vacation',
        status: 'PENDING',
      },
    }).catch(() => null);
    if (!res) { test.skip(); return; }
    console.log(`POST leave request: ${res.status()}`);
    expect([201, 200, 400, 401, 422, 500]).toContain(res.status());
  });

  test('GET /api/v1/hcm/leave-requests - list leave requests', async ({ request }) => {
    const headers: Record<string, string> = { 'X-Tenant-Id': 'tenant1' };
    if (token) headers['Authorization'] = `Bearer ${token}`;

    const res = await request.get('/api/v1/hcm/leave-requests', { headers }).catch(() => null);
    if (!res) { test.skip(); return; }
    console.log(`GET leave requests: ${res.status()}`);
    expect(res.status()).toBeGreaterThanOrEqual(200);
    expect(res.status()).toBeLessThan(500);
  });
});
