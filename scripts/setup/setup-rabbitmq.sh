#!/bin/bash
# ============================================================
# Orvion ERP — RabbitMQ Setup Script
# Creates exchanges, queues, and bindings via Management HTTP API
# ============================================================

set -euo pipefail

# Configuration
RABBIT_USER="orvion_rabbit"
RABBIT_PASS="Rabb1t@Orv10n2024"
RABBIT_HOST="localhost"
RABBIT_PORT="15672"
VHOST="orvion_vhost"
API_BASE="http://${RABBIT_HOST}:${RABBIT_PORT}/api"

echo "=== Orvion ERP RabbitMQ Topology Setup ==="

# --- Helper functions ---
create_exchange() {
    local name="$1"
    local type="${2:-topic}"
    echo "Creating exchange: ${name} (${type})"
    curl -s -u "${RABBIT_USER}:${RABBIT_PASS}" -X PUT \
        "${API_BASE}/exchanges/${VHOST}/${name}" \
        -H "Content-Type: application/json" \
        -d "{\"type\":\"${type}\",\"durable\":true,\"auto_delete\":false}"
    echo ""
}

create_queue() {
    local name="$1"
    echo "Creating queue: ${name}"
    curl -s -u "${RABBIT_USER}:${RABBIT_PASS}" -X PUT \
        "${API_BASE}/queues/${VHOST}/${name}" \
        -H "Content-Type: application/json" \
        -d "{\"durable\":true,\"auto_delete\":false,\"arguments\":{\"x-dead-letter-exchange\":\"orvion.dead-letter.exchange\"}}"
    echo ""
}

bind_queue() {
    local queue="$1"
    local exchange="$2"
    local routing_key="${3:-$queue}"
    echo "Binding queue ${queue} -> ${exchange} [${routing_key}]"
    curl -s -u "${RABBIT_USER}:${RABBIT_PASS}" -X POST \
        "${API_BASE}/bindings/${VHOST}/e/${exchange}/q/${queue}" \
        -H "Content-Type: application/json" \
        -d "{\"routing_key\":\"${routing_key}\"}"
    echo ""
}

# ============================================================
# 1. Create Exchanges
# ============================================================
echo ""
echo "--- Creating Exchanges ---"

create_exchange "orvion.finance.exchange" "topic"
create_exchange "orvion.inventory.exchange" "topic"
create_exchange "orvion.hcm.exchange" "topic"
create_exchange "orvion.sales.exchange" "topic"
create_exchange "orvion.notification.exchange" "topic"
create_exchange "orvion.manufacturing.exchange" "topic"
create_exchange "orvion.dead-letter.exchange" "direct"

# ============================================================
# 2. Create Queues
# ============================================================
echo ""
echo "--- Creating Queues ---"

# Finance
create_queue "orvion.finance.invoice.created"
create_queue "orvion.finance.payment.processed"

# Inventory
create_queue "orvion.inventory.stock.updated"
create_queue "orvion.inventory.reorder.triggered"
create_queue "orvion.inventory.purchase-order.approved"

# HCM
create_queue "orvion.hcm.employee.onboarded"
create_queue "orvion.hcm.payroll.processed"

# Sales
create_queue "orvion.sales.order.confirmed"
create_queue "orvion.sales.lead.converted"

# Notification
create_queue "orvion.notification.email.send"
create_queue "orvion.notification.push.send"

# Dead Letter
create_queue "orvion.dead-letter.queue"

# ============================================================
# 3. Create Bindings
# ============================================================
echo ""
echo "--- Creating Bindings ---"

# Finance bindings
bind_queue "orvion.finance.invoice.created" "orvion.finance.exchange"
bind_queue "orvion.finance.payment.processed" "orvion.finance.exchange"

# Inventory bindings
bind_queue "orvion.inventory.stock.updated" "orvion.inventory.exchange"
bind_queue "orvion.inventory.reorder.triggered" "orvion.inventory.exchange"
bind_queue "orvion.inventory.purchase-order.approved" "orvion.inventory.exchange"

# HCM bindings
bind_queue "orvion.hcm.employee.onboarded" "orvion.hcm.exchange"
bind_queue "orvion.hcm.payroll.processed" "orvion.hcm.exchange"

# Sales bindings
bind_queue "orvion.sales.order.confirmed" "orvion.sales.exchange"
bind_queue "orvion.sales.lead.converted" "orvion.sales.exchange"

# Notification bindings
bind_queue "orvion.notification.email.send" "orvion.notification.exchange"
bind_queue "orvion.notification.push.send" "orvion.notification.exchange"

# Dead letter binding (catch-all)
bind_queue "orvion.dead-letter.queue" "orvion.dead-letter.exchange" "#"

echo ""
echo "=== RabbitMQ topology setup complete ==="
