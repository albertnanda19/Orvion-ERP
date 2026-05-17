#!/bin/bash
set -e

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo "Building Orvion ERP..."
echo ""

# Step 1: Build common-lib
echo "Step 1: Building common-lib..."
cd "$ROOT_DIR"
mvn clean install -pl services/common-lib -q 2>&1 | tail -5
echo -e "${GREEN}✓ common-lib built${NC}"

# Step 2: Build all services in parallel
echo "Step 2: Building microservices..."
mvn clean install -pl services/config-service,services/gateway-service,services/finance-service,services/inventory-service,services/hcm-service,services/manufacturing-service,services/sales-crm-service,services/notification-service,services/reporting-service -T 4 -DskipTests -q 2>&1 | tail -5
echo -e "${GREEN}✓ All services built${NC}"

# Step 3: Build frontend
echo "Step 3: Building Angular frontend..."
cd "$ROOT_DIR/frontend/orvion-angular"
npx ng build --configuration=production 2>&1 | tail -5
echo -e "${GREEN}✓ Frontend built${NC}"

echo ""
echo -e "${GREEN}═══════════════════════════════════════╗${NC}"
echo -e "${GREEN}  Build complete!                       ${NC}"
echo -e "${GREEN}╚═══════════════════════════════════════╝${NC}"
