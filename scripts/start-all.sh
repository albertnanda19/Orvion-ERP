#!/bin/bash
set -e

GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

wait_for_service() {
    local name=$1
    local url=$2
    local max_attempts=${3:-30}
    local attempt=1
    echo -e "${YELLOW}Waiting for $name...${NC}"
    while [ $attempt -le $max_attempts ]; do
        if curl -sf "$url" > /dev/null 2>&1; then
            echo -e "${GREEN}✓ $name is UP${NC}"
            return 0
        fi
        sleep 2
        attempt=$((attempt + 1))
    done
    echo -e "${RED}✗ $name failed to start after ${max_attempts} attempts${NC}"
    return 1
}

echo "Starting Orvion ERP Services..."
echo ""

export ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
mkdir -p "$ROOT_DIR/logs"

AGENT_JAR="$ROOT_DIR/infrastructure/opentelemetry-javaagent.jar"
OTEL_ARGS=()

if [ -f "$AGENT_JAR" ]; then
    OTEL_ARGS=(-javaagent:"$AGENT_JAR" -Dotel.traces.exporter=otlp -Dotel.exporter.otlp.endpoint=http://localhost:4318 -Dotel.metrics.exporter=none -Dotel.logs.exporter=none)
    echo -e "${GREEN}OpenTelemetry agent found, tracing enabled${NC}"
else
    echo -e "${YELLOW}OpenTelemetry agent not found, tracing disabled${NC}"
fi

# Step 1: Start Config Service
echo "Step 1/5: Starting Config Service..."
java "${OTEL_ARGS[@]}" -Dotel.service.name=orvion-config-service -jar "$ROOT_DIR/services/config-service/target/config-service-1.0.0-SNAPSHOT.jar" > "$ROOT_DIR/logs/config-service.log" 2>&1 &
wait_for_service "Config Service" "http://localhost:8888/actuator/health"

# Step 2: Start Gateway
echo "Step 2/5: Starting API Gateway..."
java "${OTEL_ARGS[@]}" -Dotel.service.name=orvion-gateway-service -jar "$ROOT_DIR/services/gateway-service/target/gateway-service-1.0.0-SNAPSHOT.jar" > "$ROOT_DIR/logs/gateway-service.log" 2>&1 &
wait_for_service "Gateway Service" "http://localhost:8080/actuator/health"

# Step 3: Start domain services (parallel)
echo "Step 3/5: Starting Domain Services..."
for svc_dir in finance-service inventory-service hcm-service manufacturing-service sales-crm-service; do
    svc_name="${svc_dir%-service}"
    java "${OTEL_ARGS[@]}" -Dotel.service.name="orvion-$svc_name" -jar "$ROOT_DIR/services/$svc_dir/target/$svc_dir-1.0.0-SNAPSHOT.jar" > "$ROOT_DIR/logs/$svc_dir.log" 2>&1 &
    echo "  Started $svc_name"
done

wait_for_service "Finance Service" "http://localhost:8081/actuator/health" 40
wait_for_service "Inventory Service" "http://localhost:8082/actuator/health" 40
wait_for_service "HCM Service" "http://localhost:8083/actuator/health" 40
wait_for_service "Manufacturing Service" "http://localhost:8084/actuator/health" 40
wait_for_service "Sales CRM Service" "http://localhost:8085/actuator/health" 40

# Step 4: Start notification + reporting
echo "Step 4/5: Starting Notification and Reporting Services..."
java "${OTEL_ARGS[@]}" -Dotel.service.name=orvion-notification -jar "$ROOT_DIR/services/notification-service/target/notification-service-1.0.0-SNAPSHOT.jar" > "$ROOT_DIR/logs/notification-service.log" 2>&1 &
java "${OTEL_ARGS[@]}" -Dotel.service.name=orvion-reporting -jar "$ROOT_DIR/services/reporting-service/target/reporting-service-1.0.0-SNAPSHOT.jar" > "$ROOT_DIR/logs/reporting-service.log" 2>&1 &
echo "  Started notification, reporting"

wait_for_service "Notification Service" "http://localhost:8086/actuator/health" 40
wait_for_service "Reporting Service" "http://localhost:8087/actuator/health" 40

# Step 5: Start frontend
echo "Step 5/5: Starting Angular Frontend..."
cd "$ROOT_DIR/frontend/orvion-angular" && nohup npx ng serve --port=4200 > "$ROOT_DIR/logs/frontend.log" 2>&1 &
cd "$ROOT_DIR"
wait_for_service "Angular Frontend" "http://localhost:4200" 30

# Done
echo ""
echo -e "${GREEN}══════════════════════════════════════════════╗${NC}"
echo -e "${GREEN}    Orvion ERP - All Services Running         ${NC}"
echo -e "${GREEN}╚══════════════════════════════════════════════╝${NC}"
echo ""
echo "  Frontend:    http://localhost:4200"
echo "  API Gateway: http://localhost:8080"
echo "  Keycloak:    http://localhost:8180"
echo "  Grafana:     http://localhost:3000"
echo "  Jaeger:      http://localhost:16686"
echo "  RabbitMQ:    http://localhost:15672"
echo "  Prometheus:  http://localhost:9090"
echo ""
