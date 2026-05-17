import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '10s', target: 10 },
    { duration: '20s', target: 25 },
    { duration: '10s', target: 0 },
  ],
  thresholds: {
    http_req_duration: ['p(95)<1000', 'p(99)<2000'],
    http_req_failed: ['rate<0.05'],
  },
};

const BASE_URL = 'http://localhost:8080';

export default function () {
  // Get a fresh token for each VU iteration
  const loginRes = http.post('http://localhost:8180/realms/orvion/protocol/openid-connect/token', {
    client_id: 'api-gateway',
    client_secret: 'api-gateway-secret',
    grant_type: 'password',
    username: 'finance@orvion.com',
    password: 'Finance@123',
  });
  
  check(loginRes, { 'login 200': (r) => r.status === 200 });
  
  let token = '';
  try {
    token = JSON.parse(loginRes.body).access_token;
  } catch (e) {
    sleep(1);
    return;
  }

  const headers = {
    Authorization: `Bearer ${token}`,
    'X-Tenant-Id': 'tenant1',
    'Content-Type': 'application/json',
  };

  // Test 1: Get invoice list
  const invoiceRes = http.get(`${BASE_URL}/api/v1/finance/invoices`, { headers });
  check(invoiceRes, { 'invoices 200': (r) => r.status === 200 });

  // Test 2: Get accounts
  const accountRes = http.get(`${BASE_URL}/api/v1/finance/accounts`, { headers });
  check(accountRes, { 'accounts 200': (r) => r.status === 200 });

  // Test 3: Get dashboard
  const dashRes = http.get(`${BASE_URL}/api/v1/reports/executive-dashboard`, { headers });
  check(dashRes, { 'dashboard 200': (r) => r.status === 200 });

  // Test 4: Get products
  const productRes = http.get(`${BASE_URL}/api/v1/inventory/products`, { headers });
  check(productRes, { 'products 200': (r) => r.status === 200 });

  sleep(1);
}
