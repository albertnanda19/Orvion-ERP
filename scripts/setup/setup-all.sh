#!/bin/bash
# Master Setup Script for Orvion ERP Infrastructure
# Runs all individual setup scripts in order

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(cd "$SCRIPT_DIR/../.." && pwd)"

echo "========================================"
echo " Orvion ERP - Infrastructure Setup"
echo "========================================"
echo ""

# Step 1: PostgreSQL databases and users
echo "[1/6] Setting up PostgreSQL..."
PGPASSWORD=password psql -h localhost -U postgres -f "$PROJECT_DIR/infrastructure/postgresql/init.sql" 2>/dev/null || \
  bash "$SCRIPT_DIR/setup-postgresql.sh"
echo "  PostgreSQL setup complete."

# Step 2: Redis configuration
echo "[2/6] Setting up Redis..."
cp "$PROJECT_DIR/infrastructure/redis/redis.conf" /opt/homebrew/etc/redis-orvion.conf 2>/dev/null || true
echo "  Redis config deployed."

# Step 3: RabbitMQ topology
echo "[3/6] Setting up RabbitMQ..."
bash "$SCRIPT_DIR/setup-rabbitmq.sh"
echo "  RabbitMQ setup complete."

# Step 4: Keycloak realm and users
echo "[4/6] Setting up Keycloak..."
bash "$SCRIPT_DIR/setup-keycloak.sh"
echo "  Keycloak setup complete."

# Step 5: Elasticsearch indexes
echo "[5/6] Setting up Elasticsearch..."
bash "$SCRIPT_DIR/setup-elasticsearch.sh"
echo "  Elasticsearch setup complete."

# Step 6: Verify health
echo "[6/6] Running health check..."
bash "$SCRIPT_DIR/health-check.sh"

echo ""
echo "========================================"
echo " Orvion ERP Infrastructure Setup Complete"
echo "========================================"
