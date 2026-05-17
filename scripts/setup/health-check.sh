#!/bin/bash
# Health Check Script for Orvion ERP Infrastructure
# Checks all running services and reports status

set -uo pipefail

GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

PASS=0
FAIL=0

check() {
  local name="$1"
  local desc="$2"
  if [ $# -ge 3 ]; then
    local result="$3"
    if [ "$result" = "UP" ] || [ "$result" = "true" ] || [ "$result" = "ok" ]; then
      echo -e "  ${GREEN}✔${NC} $name ($desc)"
      PASS=$((PASS+1))
    elif [ "$result" = "PARTIAL" ]; then
      echo -e "  ${YELLOW}~${NC} $name ($desc)"
      PASS=$((PASS+1))
    else
      echo -e "  ${RED}✘${NC} $name ($desc)"
      FAIL=$((FAIL+1))
    fi
  fi
}

echo "========================================"
echo " Orvion ERP - Health Check"
echo "========================================"
echo ""

# 1. PostgreSQL
pg_isready -h localhost -q 2>/dev/null && pg_ok="UP" || pg_ok="DOWN"
check "PostgreSQL" "localhost:5432" "$pg_ok"
db_count=$(PGPASSWORD=password psql -h localhost -U postgres -t -A -c "SELECT count(*) FROM pg_database WHERE datname LIKE 'orvion_%'" 2>/dev/null || echo "0")
if [ "$db_count" -ge 7 ] 2>/dev/null; then
  check "PostgreSQL Databases" "$db_count service DBs" "UP"
else
  check "PostgreSQL Databases" "$db_count/7 service DBs" "DOWN"
fi

# 2. Redis
redis_ok=$(redis-cli -a Redi\$@Orv10n2024 ping 2>/dev/null || echo "DOWN")
if [ "$redis_ok" = "PONG" ]; then redis_ok="UP"; fi
check "Redis" "localhost:6379" "$redis_ok"

# 3. RabbitMQ
rabbit_ok=$(curl -s -u orvion_rabbit:Rabb1t@Orv10n2024 http://localhost:15672/api/healthchecks/node 2>/dev/null | python3 -c "import sys,json; d=json.load(sys.stdin); print(d.get('status','DOWN'))" 2>/dev/null || echo "DOWN")
check "RabbitMQ" "localhost:5672 (AMQP)" "$rabbit_ok"
queue_count=$(curl -s -u orvion_rabbit:Rabb1t@Orv10n2024 http://localhost:15672/api/queues/orvion_vhost 2>/dev/null | python3 -c "import sys,json; print(len(json.load(sys.stdin)))" 2>/dev/null || echo "0")
if [ "$queue_count" -ge 10 ]; then
  check "RabbitMQ Queues" "$queue_count queues" "UP"
else
  check "RabbitMQ Queues" "$queue_count/12 queues" "DOWN"
fi

# 4. Keycloak
kc_ok=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8180/realms/master/.well-known/openid-configuration 2>/dev/null)
if [ "$kc_ok" = "200" ]; then
  check "Keycloak" "localhost:8180" "UP"
  user_count=$($HOME/keycloak-24.0.4/bin/kcadm.sh get users -r orvion 2>/dev/null | python3 -c "import sys,json; print(len(json.load(sys.stdin)))" 2>/dev/null || echo "0")
  check "Keycloak Users" "$user_count users in orvion realm" "UP"
else
  check "Keycloak" "localhost:8180" "DOWN"
fi

# 5. Elasticsearch
es_ok=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:9200 2>/dev/null)
if [ "$es_ok" = "200" ]; then
  check "Elasticsearch" "localhost:9200" "UP"
  idx_count=$(curl -s http://localhost:9200/_cat/indices/orvion-*?format=json 2>/dev/null | python3 -c "import sys,json; print(len(json.load(sys.stdin)))" 2>/dev/null || echo "0")
  check "Elasticsearch Indexes" "$idx_count indexes" "UP"
else
  check "Elasticsearch" "localhost:9200" "DOWN"
fi

# 6. Prometheus
prom_ok=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:9090/-/ready 2>/dev/null)
if [ "$prom_ok" = "200" ]; then
  check "Prometheus" "localhost:9090" "UP"
else
  check "Prometheus" "localhost:9090" "DOWN"
fi

# 7. Grafana
graf_ok=$(curl -s http://localhost:3000/api/health 2>/dev/null | python3 -c "import sys,json; print(json.load(sys.stdin).get('database','DOWN'))" 2>/dev/null)
check "Grafana" "localhost:3000" "$graf_ok"

echo ""
echo "----------------------------------------"
echo -e " Results: ${GREEN}$PASS passed${NC}, ${RED}$FAIL failed${NC}"
echo "----------------------------------------"
echo ""

# Port summary
echo "Port Summary:"
echo "  PostgreSQL:    5432"
echo "  Redis:         6379"
echo "  RabbitMQ:      5672 (AMQP), 15672 (Management)"
echo "  Keycloak:      8180"
echo "  Elasticsearch: 9200"
echo "  Prometheus:    9090"
echo "  Grafana:       3000"
echo ""

exit $FAIL
