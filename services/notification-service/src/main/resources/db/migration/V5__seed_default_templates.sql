-- Seed default notification templates (placeholders escaped for Flyway)
INSERT INTO notification_templates (id, tenant_id, template_code, subject, body, channel, event_type, language, active)
SELECT gen_random_uuid(), 'default', 'invoice-created', 'Invoice #{invoiceNumber} Created',
    '<html xmlns:th="http://www.thymeleaf.org"><body>Invoice template</body></html>',
    'EMAIL', 'orvion.finance.invoice.created', 'en', true
WHERE NOT EXISTS (SELECT 1 FROM notification_templates WHERE template_code = 'invoice-created');

INSERT INTO notification_templates (id, tenant_id, template_code, subject, body, channel, event_type, language, active)
SELECT gen_random_uuid(), 'default', 'payment-processed', 'Payment Confirmed - #{paymentReference}',
    '<html xmlns:th="http://www.thymeleaf.org"><body>Payment template</body></html>',
    'EMAIL', 'orvion.finance.payment.processed', 'en', true
WHERE NOT EXISTS (SELECT 1 FROM notification_templates WHERE template_code = 'payment-processed');

INSERT INTO notification_templates (id, tenant_id, template_code, subject, body, channel, event_type, language, active)
SELECT gen_random_uuid(), 'default', 'payroll-processed', 'Payslip Processed',
    '<html xmlns:th="http://www.thymeleaf.org"><body>Payroll template</body></html>',
    'EMAIL', 'orvion.hcm.payroll.processed', 'en', true
WHERE NOT EXISTS (SELECT 1 FROM notification_templates WHERE template_code = 'payroll-processed');

INSERT INTO notification_templates (id, tenant_id, template_code, subject, body, channel, event_type, language, active)
SELECT gen_random_uuid(), 'default', 'low-stock-alert', 'Low Stock Alert - #{sku}',
    '<html xmlns:th="http://www.thymeleaf.org"><body>Stock alert template</body></html>',
    'EMAIL', 'orvion.inventory.reorder.triggered', 'en', true
WHERE NOT EXISTS (SELECT 1 FROM notification_templates WHERE template_code = 'low-stock-alert');

INSERT INTO notification_templates (id, tenant_id, template_code, subject, body, channel, event_type, language, active)
SELECT gen_random_uuid(), 'default', 'leave-approved', 'Leave Request Approved',
    '<html xmlns:th="http://www.thymeleaf.org"><body>Leave template</body></html>',
    'EMAIL', 'orvion.hcm.employee.onboarded', 'en', true
WHERE NOT EXISTS (SELECT 1 FROM notification_templates WHERE template_code = 'leave-approved');

INSERT INTO notification_templates (id, tenant_id, template_code, subject, body, channel, event_type, language, active)
SELECT gen_random_uuid(), 'default', 'work-order-completed', 'Work Order Completed',
    '<html xmlns:th="http://www.thymeleaf.org"><body>Work order template</body></html>',
    'EMAIL', 'orvion.manufacturing.work.order.completed', 'en', true
WHERE NOT EXISTS (SELECT 1 FROM notification_templates WHERE template_code = 'work-order-completed');

INSERT INTO notification_templates (id, tenant_id, template_code, subject, body, channel, event_type, language, active)
SELECT gen_random_uuid(), 'default', 'employee-onboarded', 'Welcome to Orvion!',
    '<html xmlns:th="http://www.thymeleaf.org"><body>Employee onboarding template</body></html>',
    'EMAIL', 'orvion.hcm.employee.onboarded', 'en', true
WHERE NOT EXISTS (SELECT 1 FROM notification_templates WHERE template_code = 'employee-onboarded');

INSERT INTO notification_templates (id, tenant_id, template_code, subject, body, channel, event_type, language, active)
SELECT gen_random_uuid(), 'default', 'order-confirmed', 'Order #{orderNumber} Confirmed',
    '<html xmlns:th="http://www.thymeleaf.org"><body>Order template</body></html>',
    'EMAIL', 'orvion.sales.order.confirmed', 'en', true
WHERE NOT EXISTS (SELECT 1 FROM notification_templates WHERE template_code = 'order-confirmed');
